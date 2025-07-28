package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.io.File;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Configuration
@RequiredArgsConstructor
public class S3Util {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.pdf.prefix}")
    private String PDF_CONTENT_PREFIX;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.default-path}")
    private String s3DefaultPath;

    @Value("${spring.cloud.aws.cloudfront.path}")
    private String cloudFrontPath;

    /**
     * S3 key를 전달받아 해당 객체의 최종 URL을 반환 (SDK v2 방식)
     *
     * @param key S3 객체 키 (예: 'folder/subfolder/filename.png')
     * @return 해당 객체에 접근 가능한 URL
     */
    public String getS3ObjectUrl(String key) {
        try {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            return s3Client.utilities().getUrl(request).toString();
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "S3 URL을 가져오는 데 실패했습니다.");
        }
    }

    /**
     * Document에 해당하는 PDF 파일을 S3에 업로드 (SDK v2 방식)
     *
     * @param document 업로드할 PDF 파일에 대한 정보를 가진 Document 객체
     * @param file     업로드할 File 객체
     * @return CloudFront URL
     */
    public String uploadPdf(Document document, File file) {
        String fileName = generateUniqueFileName(document);
        String finalKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + '/'
                + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(finalKey)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            String directUrl = getS3ObjectUrl(finalKey);
            return directUrl.replace(s3DefaultPath, cloudFrontPath);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "PDF 업로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * 중복되지 않는 고유한 파일명을 생성하는 메서드
     *
     * @param document PDF 파일이 속할 Document 객체
     * @return 중복되지 않는 파일명 (예: "document.pdf", "document (1).pdf", "document (2).pdf")
     */
    private String generateUniqueFileName(Document document) {
        String baseName = document.getName();
        String extension = ".pdf";

        // 기존 PDF 파일명들을 수집
        Set<String> existingFileNames = document.getPdfs().stream()
                .map(pdf -> {
                    String url = pdf.getPdfUrl();
                    // URL에서 파일명 추출 (마지막 '/' 이후의 부분)
                    int lastSlashIndex = url.lastIndexOf('/');
                    return lastSlashIndex != -1 ? url.substring(lastSlashIndex + 1) : url;
                })
                .collect(java.util.stream.Collectors.toSet());

        String fileName = baseName + extension;

        // 중복되지 않는 파일명이 될 때까지 번호를 증가시킴
        int counter = 1;
        while (existingFileNames.contains(fileName)) {
            fileName = baseName + " (" + counter + ")" + extension;
            counter++;
        }

        return fileName;
    }

    /**
     * S3에서 객체의 파일명을 변경하고 새로운 URL을 반환하는 메서드 (복사 후 삭제)
     *
     * @param document    PDF가 속한 Document 객체
     * @param oldUrl      기존 PDF URL
     * @param newFileName 새로운 파일명
     * @return 변경된 파일의 새로운 URL
     */
    public String renameS3ObjectAndGetUrl(Document document, String oldUrl, String newFileName) {
        String oldKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + '/'
                + extractFileNameFromUrl(oldUrl);

        String newKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + '/'
                + newFileName;

        try {
            // 1. 객체 복사
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(oldKey)
                    .destinationBucket(bucketName)
                    .destinationKey(newKey)
                    .build();
            s3Client.copyObject(copyObjectRequest);

            // 2. 기존 객체 삭제
            deleteObject(oldKey);

            // 3. 새로운 URL 반환
            String directUrl = getS3ObjectUrl(newKey);
            return directUrl.replace(s3DefaultPath, cloudFrontPath);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "S3 파일 이름 변경 중 오류가 발생했습니다.");
        }
    }

    /**
     * URL에서 파일명을 추출하는 메서드
     */
    private String extractFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        return lastSlashIndex != -1 ? url.substring(lastSlashIndex + 1) : url;
    }

    /**
     * PDF 파일을 S3에서 삭제하는 메서드
     *
     * @param pdf 삭제할 PDF 객체
     */
    public void deletePdfFromS3(Pdf pdf) {
        String url = pdf.getPdfUrl();
        String fileName = extractFileNameFromUrl(url);
        String key = PDF_CONTENT_PREFIX
                + pdf.getDocument().getOrder().getId() + '/' + pdf.getDocument().getId() + '/'
                + fileName;
        deleteObject(key);
    }

    /**
     * S3 객체를 삭제하는 내부 헬퍼 메서드 (SDK v2 방식)
     *
     * @param key 삭제할 S3 객체 키
     */
    private void deleteObject(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "S3 객체 삭제 중 오류가 발생했습니다.");
        }
    }
}
