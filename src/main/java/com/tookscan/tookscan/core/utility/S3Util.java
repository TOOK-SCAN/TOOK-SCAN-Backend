package com.tookscan.tookscan.core.utility;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.tookscan.tookscan.core.dto.PdfFileDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Util {
    private final AmazonS3Client amazonS3Client;

    private final String IMAGE_CONTENT_PREFIX = "image/";

    @Value("${cloud.aws.s3.pdf.prefix}")
    private String PDF_CONTENT_PREFIX;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url}")
    private String bucketUrl;

    @Value("${cloud.aws.s3.pdf.expiration-seconds}")
    private Long pdfExpirationSeconds;

    @Value("${cloud.aws.cloudfront.domain}")
    private String domain;

    @Value("${cloud.aws.cloudfront.private-key-path}")
    private String privateKeyPath;

    @Value("${cloud.aws.cloudfront.key-id}")
    private String keyId;

    @Value("${cloud.aws.s3.default-path}")
    private String s3DefaultPath;

    @Value("${cloud.aws.cloudfront.path}")
    private String cloudFrontPath;

    /**
     * S3 key를 전달받아 해당 객체의 최종 URL을 반환 (AmazonS3Client가 제공하는 기본 메서드를 사용)
     *
     * @param key S3 객체 키 (예: 'folder/subfolder/filename.png')
     * @return 해당 객체에 접근 가능한 URL
     */
    public String getS3ObjectUrl(String key) {
        return amazonS3Client.getUrl(bucketName, key).toString();
    }

    /**
     * 이미지 경로를 prefix(IMAGE_CONTENT_PREFIX)로 묶어 최종 접근 가능한 URL을 반환
     *
     * @param imageName 실제 이미지 파일 이름 (예: 'myphoto.png')
     * @return 이미지 객체에 접근 가능한 URL
     */
    public String getImageUrl(String imageName) {
        // 예: "image/myphoto.png"
        String finalKey = IMAGE_CONTENT_PREFIX + imageName;
        return amazonS3Client.getUrl(bucketName, finalKey).toString();
    }

    /**
     * PDF 경로를 prefix(PDF_CONTENT_PREFIX)로 묶어 최종 접근 가능한 URL을 반환
     *
     * @param document PDF 파일을 가지고 있는 Document 객체
     * @return PDF 객체에 접근 가능한 URL
     */
    public PdfFileDto downloadPdfFile(Document document) {
        // 존재하지 않는 PDF 파일인 경우
        if (!doesObjectExist(document)) {
            throw new CommonException(ErrorCode.NOT_FOUND_PDF_FILE);
        }
        String finalKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + '/'
                + document.getName() + ".pdf";

        S3Object s3Object = amazonS3Client.getObject(bucketName, finalKey);

        try (InputStream inputStream = s3Object.getObjectContent()) {
            byte[] fileBytes = inputStream.readAllBytes();
            String fileName = document.getName() + "_" + document.getId() + ".pdf";
            return new PdfFileDto(fileName, fileBytes, "application/pdf");
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean doesObjectExist(Document doc) {
        String key = PDF_CONTENT_PREFIX + doc.getOrder().getId() + '/' + doc.getName() + '_' + doc.getId() + ".pdf";
        return amazonS3Client.doesObjectExist(bucketName, key);
    }


    /**
     * Document에 해당하는 PDF 파일을 S3에 업로드하는 메서드
     *
     * @param document 업로드할 PDF 파일에 대한 정보를 가진 Document 객체
     * @param file     업로드할 MultipartFile
     */
    public String uploadPdf(Document document, File file) {
        String fileName = generateUniqueFileName(document);

        String finalKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + '/'
                + fileName;
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setContentType("application/pdf");

            try (InputStream inputStream = new FileInputStream(file)) {
                amazonS3Client.putObject(bucketName, finalKey, inputStream, metadata);
            }

            String directUrl = amazonS3Client.getUrl(bucketName, finalKey).toString();
            return directUrl.replace(s3DefaultPath, cloudFrontPath);
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
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

        // 새로운 S3 키 생성
        String newKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + '/'
                + newFileName;

        try {
            // S3에서 객체 복사
            amazonS3Client.copyObject(bucketName, oldKey, bucketName, newKey);

            // 기존 객체 삭제
            amazonS3Client.deleteObject(bucketName, oldKey);

            // 새로운 URL 반환
            String directUrl = amazonS3Client.getUrl(bucketName, newKey).toString();
            return directUrl.replace(s3DefaultPath, cloudFrontPath);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
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
        try {
            String url = pdf.getPdfUrl();
            String fileName = extractFileNameFromUrl(url);

            String key = PDF_CONTENT_PREFIX
                    + pdf.getDocument().getOrder().getId() + '/' + pdf.getDocument().getId() + '/'
                    + fileName;

            // S3에서 객체 삭제
            amazonS3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
