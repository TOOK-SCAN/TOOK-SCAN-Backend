package com.tookscan.tookscan.core.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.dto.OauthJsonWebTokenDto;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpServletUtil {

    @Value("${web-engine.client-url}")
    private String clientUrl;

    @Value("${web-engine.cookie-domain}")
    private String cookieDomain;

    @Value("${web-engine.cookie.access-token-name}")
    private String accessTokenCookieName;

    @Value("${web-engine.cookie.refresh-token-name}")
    private String refreshTokenCookieName;

    @Value("${web-engine.cookie.temporary-token-name}")
    private String temporaryTokenCookieName;

    @Value("${json-web-token.refresh-token-expire-period}")
    private Long refreshTokenExpirePeriod;

    private final ObjectMapper objectMapper;

    public void onSuccessRedirectResponseWithJWTCookie(
            String redirectPath,
            HttpServletResponse response,
            OauthJsonWebTokenDto tokenDto
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.CREATED.value());

        // 최초 로그인이 아닐 시
        if (tokenDto.getTemporaryToken() == null) {
            CookieUtil.addCookie(
                    response,
                    cookieDomain,
                    accessTokenCookieName,
                    tokenDto.getAccessToken()
            );

            CookieUtil.addSecureCookie(
                    response,
                    cookieDomain,
                    refreshTokenCookieName,
                    tokenDto.getRefreshToken(),
                    (int) (refreshTokenExpirePeriod / 1000L)
            );
        }
        // 최초 로그인 시 (임시 토큰 발급)
        else {
            CookieUtil.addCookie(
                    response,
                    cookieDomain,
                    temporaryTokenCookieName,
                    tokenDto.getTemporaryToken()
            );
        }

        response.sendRedirect(String.format("%s/%s", clientUrl, redirectPath));
    }

    public void onSuccessBodyResponseWithJWTCookie(
            HttpServletResponse response,
            DefaultJsonWebTokenDto tokenDto
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.CREATED.value());

        CookieUtil.addCookie(
                response,
                cookieDomain,
                accessTokenCookieName,
                tokenDto.getAccessToken()
        );
        CookieUtil.addSecureCookie(
                response,
                cookieDomain,
                refreshTokenCookieName,
                tokenDto.getRefreshToken(),
                (int) (refreshTokenExpirePeriod / 10000L) // 기존 기한의 10분의 1로 설정 (로그인 유지 체크 안함)
        );

        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("data", null);
        result.put("error", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    public void onSuccessBodyResponseWithDeletedJWTCookie(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.CREATED.value());

        CookieUtil.deleteCookie(
                request,
                response,
                cookieDomain,
                accessTokenCookieName
        );
        CookieUtil.deleteCookie(
                request,
                response,
                cookieDomain,
                refreshTokenCookieName
        );

        CookieUtil.deleteCookie(
                request,
                response,
                cookieDomain,
                temporaryTokenCookieName
        );

        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("data", null);
        result.put("error", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    public void onSuccessBodyResponseWithJWTCookieRememberMeTrue(
            HttpServletResponse response,
            DefaultJsonWebTokenDto tokenDto
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.CREATED.value());

        CookieUtil.addCookie(
                response,
                cookieDomain,
                accessTokenCookieName,
                tokenDto.getAccessToken()
        );
        CookieUtil.addSecureCookie(
                response,
                cookieDomain,
                refreshTokenCookieName,
                tokenDto.getRefreshToken(),
                (int) (refreshTokenExpirePeriod / 1000L)
        );

        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("data", null);
        result.put("error", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    public void onSuccessBodyResponseWithJWTBody(
            HttpServletResponse response,
            DefaultJsonWebTokenDto tokenDto,
            ESecurityRole role

    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.CREATED.value());

        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("data", Map.of(
                        "access_token", tokenDto.getAccessToken(),
                        "refresh_token", tokenDto.getRefreshToken(),
                        "account_type", role
                )
        );
        result.put("error", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    public void onSuccessBodyResponseWithOauthJWTBody(
            HttpServletResponse response,
            OauthJsonWebTokenDto tokenDto
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.CREATED.value());

        Map<String, Object> data = new HashMap<>();
        data.put("temporary_token", tokenDto.getTemporaryToken());
        data.put("access_token", tokenDto.getAccessToken());
        data.put("refresh_token", tokenDto.getRefreshToken());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("error", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    public void onSuccessBodyResponse(
            HttpServletResponse response,
            HttpStatus httpStatus
    ) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus.value());

        Map<String, Object> result = new HashMap<>();

        if (httpStatus != HttpStatus.NO_CONTENT) {
            result.put("success", true);
            result.put("data", null);
            result.put("error", null);

            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }

    public static String getClientIp(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xForwardedForHeader.split(",")[0].trim();
    }
}
