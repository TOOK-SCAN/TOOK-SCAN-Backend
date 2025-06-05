package com.tookscan.tookscan.core.constant;

import java.util.List;

public class Constants {

    // JWT
    public static String ACCOUNT_ID_ATTRIBUTE_NAME = "ACCOUNT_ID";
    public static String ACCOUNT_ID_CLAIM_NAME = "aid";
    public static String ACCOUNT_ROLE_CLAIM_NAME = "rol";

    // HEADER
    public static String BEARER_PREFIX = "Bearer ";
    public static String AUTHORIZATION_HEADER = "Authorization";

    // TOKEN
    public static String ACCESS_TOKEN = "access_token";
    public static String REFRESH_TOKEN = "refresh_token";
    public static String TEMPORARY_TOKEN = "temporary_token";
    public static String REMEMBER_ME = "remember_me";

    // Oauth2 Href URL
    public static String KAKAO_OAUTH2_HREF = "/oauth2/authorization/kakao";
    public static String GOOGLE_OAUTH2_HREF = "/oauth2/authorization/google";

    // Additional Info Input Url
    public static String ADDITIONAL_INFO_INPUT_PATH = "/auth/join?provider=easy";
    
    /**
     * 인증이 필요 없는 URL
     */
    public static List<String> NO_NEED_AUTH_URLS = List.of(
            // Authentication/Authorization
            "/oauth2/authorization/kakao",
            "/login/oauth2/code/kakao",
            "/oauth2/authorization/google",
            "/login/oauth2/code/google",
            "/v1/auth/login",
            "/v1/auth/reissue/token",
            "/v1/auth/authentication-code",
            "/v1/auth/users/sign-up-default",
            "/v1/auth/admins/sign-up-default",
            "/v1/auth/users/sign-up-oauth",
            "/v1/users/terms/overviews",
            "/v1/auth/verification/serial-id",
            "/v1/auth/existence/serial-id",
            "/v1/auth/reissue/authentication-code",
            "/v1/auth/reissue/password",
            "/v1/auth/verification/user",

            // Guest
            "/v1/guests/**",

            // Test Email
            "/v1/test-email",
            "/v1/validation/test-email",

            // Payment
            "/v1/payments",

            // Swagger
            "/api-docs.html",
            "/api-docs/**",
            "/swagger-ui/**",
            "/v3/**",

            // Health Check
            "/actuator/health"
    );

    /**
     * Swagger 에서 사용하는 URL
     */
    public static List<String> SWAGGER_URLS = List.of(
            "/api-docs.html",
            "/api-docs",
            "/swagger-ui",
            "/v3"
    );

    /**
     * 사용자 URL
     */
    public static List<String> USER_URLS = List.of(
            "/v1/users/**"
    );

    public static List<String> ADMIN_URLS = List.of(
            "/v1/admins/**"
    );

    public static List<String> COMMON_URLS = List.of(
            "/v1/**"
    );
}
