package com.tookscan.tookscan.core.utility;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.tookscan.tookscan.core.dto.PdfFileDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

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
        String finalKey =
                PDF_CONTENT_PREFIX + document.getOrder().getId() + '/' + document.getName() + '_' + document.getId()
                        + ".pdf";

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
    public String uploadPdf(Document document, MultipartFile file) {
        String finalKey = PDF_CONTENT_PREFIX
                + document.getOrder().getId() + '/'
                + document.getName() + '_' + document.getId() + ".pdf";
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3Client.putObject(bucketName, finalKey, file.getInputStream(), metadata);

            return amazonS3Client.getUrl(bucketName, finalKey).toString();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
