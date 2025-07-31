package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
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
     * PDF ID 기반 폴더 구조: orders/{orderId}/documents/{documentId}/pdfs/{pdfId}/{fileName}
     *
     * @param document 업로드할 PDF 파일에 대한 정보를 가진 Document 객체
     * @param file     업로드할 File 객체
     * @param pdfId    생성된 PDF 엔티티의 ID
     * @return CloudFront URL
     */
    public String uploadPdf(Document document, File file, Long pdfId) {
        String fileName = document.getName() + ".pdf";
        String finalKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/' + document.getId() + "/pdfs/" + pdfId + '/'
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
     * PDF 파일을 S3에서 삭제하는 메서드
     *
     * @param pdf 삭제할 PDF 객체
     */
    public void deletePdfFromS3(Pdf pdf) {
        String url = pdf.getPdfUrl();
        String pdfId = String.valueOf(pdf.getId());
        String key = PDF_CONTENT_PREFIX
                + pdf.getDocument().getOrder().getId() + '/' + pdf.getDocument().getId() + "/pdfs/" + pdfId + '/'
                + pdf.getDocument().getName() + ".pdf";
        deleteObject(key);
    }

    /**
     * S3 객체 존재 여부를 확인하는 메서드
     *
     * @param key 확인할 S3 객체 키
     * @return 객체가 존재하면 true, 존재하지 않으면 false
     */
    private boolean objectExists(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            return false;
        }
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
