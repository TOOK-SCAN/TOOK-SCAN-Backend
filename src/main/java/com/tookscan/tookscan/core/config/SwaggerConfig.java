package com.tookscan.tookscan.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String JWT_SCHEMA_NAME = "JWT TOKEN";
    private static final String VERSION = "0.0.1";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${web-engine.server-url}")
    private String serverUrl;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(loginEndpointCustomiser())
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/v1/admins/**")
                .build();
    }

    @Bean
    public OpenApiCustomizer loginEndpointCustomiser() {
        return openAPI -> {
            // 로그인 API 정의
            Operation loginOperation = new Operation()
                    .summary("로그인")
                    .description("아이디와 비밀번호로 로그인합니다.")
                    .addTagsItem("Authentication");

            // 요청 스키마 정의
            ObjectSchema loginSchema = new ObjectSchema();
            loginSchema.addProperty("serial_id", new StringSchema().description("아이디"));
            loginSchema.addProperty("password", new StringSchema().description("비밀번호"));

            // 요청 바디 설정
            RequestBody requestBody = new RequestBody()
                    .required(true)
                    .content(new Content()
                            .addMediaType("application/x-www-form-urlencoded",
                                    new MediaType().schema(loginSchema)));

            loginOperation.requestBody(requestBody);

            // 응답 설정
            ApiResponses apiResponses = new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                            .description("로그인 성공 - 쿠키에 토큰 설정"))
                    .addApiResponse("401", new ApiResponse()
                            .description("로그인 실패 - 잘못된 인증 정보"))
                    .addApiResponse("400", new ApiResponse()
                            .description("잘못된 요청 형식"));

            loginOperation.responses(apiResponses);

            // 보안 요구사항 제거 (로그인 API는 인증 불필요)
            loginOperation.security(List.of());

            // OpenAPI에 경로 추가
            PathItem pathItem = new PathItem().post(loginOperation);
            openAPI.path("/v1/auth/login", pathItem);
        };
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TookScan Main Server API")
                        .description("TookScan Main Server API 명세서 입니다.")
                        .version(VERSION))
                .components(new Components()
                        .addSecuritySchemes(JWT_SCHEMA_NAME,
                                new SecurityScheme()
                                        .name(AUTHORIZATION_HEADER)
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEMA_NAME))
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url(serverUrl)));
    }
}
