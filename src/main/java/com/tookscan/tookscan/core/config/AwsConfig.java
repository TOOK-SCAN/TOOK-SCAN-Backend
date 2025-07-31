package com.tookscan.tookscan.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * 주입받은 accessKey와 secretKey로 AWS 자격 증명 공급자 Bean을 생성합니다.
     */
    @Bean
    public StaticCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    /**
     * S3TransferManager가 필요로 하는 비동기 S3 클라이언트 Bean을 생성합니다. 위에서 만든 자격 증명과 yml의 리전 정보를 사용합니다.
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .credentialsProvider(awsCredentialsProvider())
                .region(Region.of(region))
                .build();
    }

    /**
     * S3TransferManager를 Spring Bean으로 등록합니다. 이 때, Spring Boot가 application.yml 설정을 기반으로 자동 생성한 S3Client Bean을 파라미터로
     * 주입받습니다.
     *
     * @param s3AsyncClient Spring이 자동으로 구성한 S3 클라이언트
     * @return 설정된 S3TransferManager
     */
    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
    }
}
