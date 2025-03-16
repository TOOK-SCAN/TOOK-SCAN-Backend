package com.tookscan.tookscan.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${rest.client.connection-timeout}")
    private int CONNECTION_TIMEOUT;

    @Value("${rest.client.read-timeout}")
    private int READ_TIMEOUT;

    @Bean
    public RestClient restClient() {
        // HttpComponentsClientHttpRequestFactory에 타임아웃 설정
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT * 1000);
        requestFactory.setReadTimeout(READ_TIMEOUT * 1000);

        // 설정된 requestFactory를 사용하여 RestClient 생성
        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
