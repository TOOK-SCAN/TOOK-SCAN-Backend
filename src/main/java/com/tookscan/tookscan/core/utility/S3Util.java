package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

@Slf4j
@Configuration
public class S3Util {

    private final S3Client s3Client;
    private final CloudFrontUtilities cloudFrontUtilities;
    private final S3TransferManager s3TransferManager;

    private final String pdfContentPrefix;
    private final String bucketName;
    private final String cloudFrontDomain;
    private final String cloudFrontKeyPairId;
    private final Resource privateKeyResource;

    private static final Integer PDF_EXPIRATION_DATE_FOR_ADMIN = 30; // 관리자용 PDF 만료일 (30일)
    private static final Integer PDF_EXPIRATION_DATE = 15;

    public S3Util(
            S3Client s3Client,
            S3TransferManager s3TransferManager,
            @Value("${spring.cloud.aws.s3.pdf.prefix}") String pdfContentPrefix,
            @Value("${spring.cloud.aws.s3.bucket}") String bucketName,
            @Value("${spring.cloud.aws.cloudfront.domain}") String cloudFrontDomain,
            @Value("${spring.cloud.aws.cloudfront.key-pair-id}") String cloudFrontKeyPairId,
            @Value("${spring.cloud.aws.cloudfront.private-key-path}") Resource privateKeyResource) {
        this.s3Client = s3Client;
        this.s3TransferManager = s3TransferManager;
        this.pdfContentPrefix = pdfContentPrefix;
        this.bucketName = bucketName;
        this.cloudFrontDomain = cloudFrontDomain;
        this.cloudFrontKeyPairId = cloudFrontKeyPairId;
        this.privateKeyResource = privateKeyResource;
        this.cloudFrontUtilities = CloudFrontUtilities.create();
    }

    /**
     * PDF 파일을 S3에 업로드하고 서명되지 않은 일반 CloudFront URL을 반환합니다. ❕ 중요: 이 URL로 파일에 접근하려면 해당 경로의 CloudFront 동작(Behavior) 설정에서
     * '뷰어 액세스 제한(Restrict Viewer Access)'이 '아니요(No)'로 설정되어 있어야 합니다.
     *
     * @param document       업로드할 파일의 메타데이터
     * @param file           업로드할 실제 파일
     * @param uniqueFileName S3에 저장될 고유 파일명 (확장자 포함)
     * @return 생성된 일반 CloudFront URL
     */
    public String uploadAndGetPublicUrl(Document document, File file, String uniqueFileName) {
        String s3Key = buildPdfS3Key(document, uniqueFileName);

        try {
            // 1. S3에 파일 업로드
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .putObjectRequest(b -> b.bucket(bucketName).key(s3Key))
                    .source(file)
                    .build();

            FileUpload fileUpload = s3TransferManager.uploadFile(uploadFileRequest);

            fileUpload.completionFuture().join();

            log.info("Public PDF uploaded to S3. S3 Key: {}", s3Key);

            // 2. 서명되지 않은 단순 CloudFront URL 생성하여 반환
            return String.format("https://%s/%s", cloudFrontDomain, s3Key);

        } catch (Exception e) {
            log.error("Public PDF 업로드 실패. S3 Key: {}", s3Key, e);
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "공개 PDF 업로드 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    /**
     * PDF 파일을 S3에 업로드하고 CloudFront Signed URL을 반환합니다.
     *
     * @param document       업로드할 파일의 메타데이터
     * @param file           업로드할 실제 파일
     * @param uniqueFileName S3에 저장될 고유 파일명 (확장자 포함, 예: "uuid.pdf")
     * @return 생성된 CloudFront Signed URL
     */
    public String uploadPdfAndGetSignedUrlForAdmin(Document document, File file, String uniqueFileName,
                                                   String originalFileName) {
        String s3Key = buildPdfS3Key(document, uniqueFileName);

        try {
            String contentDisposition = "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(originalFileName,
                    java.nio.charset.StandardCharsets.UTF_8);

            // 1. S3에 파일 업로드
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .putObjectRequest(b -> b.bucket(bucketName)
                            .key(s3Key)
                            .contentDisposition(contentDisposition))
                    .source(file)
                    .build();

            FileUpload fileUpload = s3TransferManager.uploadFile(uploadFileRequest);

            fileUpload.completionFuture().join();

            log.info("PDF uploaded to S3. S3 Key: {}", s3Key);

            // 2. CloudFront Signed URL 생성 (30일 유효)
            return generateCloudFrontSignedUrl(s3Key, PDF_EXPIRATION_DATE_FOR_ADMIN);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.EXTERNAL_SERVER_ERROR,
                    "PDF 업로드 및 URL 생성 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    public String getSignedUrlForUser(Pdf pdf) {
        if (pdf.getStoredFileName() == null || pdf.getStoredFileName().isBlank()) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "PDF의 S3 고유 파일명 정보가 없습니다.");
        }
        // [수정] pdf.getName() -> pdf.getStoredFileName()
        String s3Key = buildPdfS3Key(pdf.getDocument(), pdf.getStoredFileName());
        return generateCloudFrontSignedUrl(s3Key, PDF_EXPIRATION_DATE);
    }

    /**
     * PDF 파일을 S3에서 삭제합니다.
     *
     * @param pdf 삭제할 PDF 엔티티 (DB에 저장된 고유 파일명을 포함해야 함)
     */
    public void deletePdfFromS3(Pdf pdf) {
        // [수정] pdf.getName() 대신 DB에 저장된 고유 파일명(storedFileName)을 사용하도록 변경
        if (pdf.getStoredFileName() == null || pdf.getStoredFileName().isBlank()) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "삭제할 파일의 S3 고유 파일명 정보가 없습니다.");
        }
        // [수정] pdf.getName() -> pdf.getStoredFileName()
        String s3Key = buildPdfS3Key(pdf.getDocument(), pdf.getStoredFileName());
        deleteObject(s3Key);
    }

    private String buildPdfS3Key(Document document, String uniqueFileName) {
        return pdfContentPrefix
                + document.getOrder().getId() + '/'
                + document.getId() + '/'
                + uniqueFileName;
    }

    /**
     * CloudFront Signed URL을 생성합니다. (공식 문서의 간결한 방식 사용)
     *
     * @param objectKey    S3 객체 키 (CloudFront 경로)
     * @param daysToExpire URL 만료 기한 (일 단위)
     * @return 생성된 Signed URL
     */
    private String generateCloudFrontSignedUrl(String objectKey, long daysToExpire) {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyResource);

            URI uri = new URI("https", cloudFrontDomain, "/" + objectKey, null);
            String resourceUrl = uri.toASCIIString();
            Instant expirationTime = Instant.now().plus(daysToExpire, ChronoUnit.DAYS);
            log.debug(resourceUrl);

            // CannedSignerRequest를 사용하여 서명 요청을 빌드합니다.
            CannedSignerRequest request = CannedSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(privateKey)
                    .keyPairId(cloudFrontKeyPairId)
                    .expirationDate(expirationTime)
                    .build();

            // 유틸리티 클래스를 사용하여 서명된 URL을 가져옵니다.
            SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);

            log.atInfo()
                    .log("CloudFront Signed URL이 생성되었습니다.");
            return signedUrl.url();

        } catch (Exception e) {
            throw new CommonException(ErrorCode.EXTERNAL_SERVER_ERROR,
                    "CloudFront Signed URL 생성 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    /**
     * Resource에서 PrivateKey 객체를 로드하는 헬퍼 메서드
     *
     * @param resource Private Key 파일 리소스 (PEM 형식)
     * @return PrivateKey 객체
     * @throws Exception 키 로딩/파싱 중 예외 발생
     */
    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] keyBytes = inputStream.readAllBytes();
            String privateKeyPEM = new String(keyBytes);

            // PEM 형식에서 헤더/푸터 및 줄바꿈 제거
            String privateKeyContent = privateKeyPEM
                    .replaceAll("\\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");

            byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
    }

    /**
     * S3 객체를 삭제하는 내부 헬퍼 메서드
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
            log.error("S3 객체 삭제 중 오류 발생. S3 Key: {}, Error: {}", key, e.getMessage());
            throw new CommonException(ErrorCode.EXPIRED_TOKEN_ERROR, "S3 객체 삭제 중 오류가 발생했습니다." + e.getMessage());
        }
    }
}