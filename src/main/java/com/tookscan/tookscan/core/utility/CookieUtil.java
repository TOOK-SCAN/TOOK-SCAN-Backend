package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

/**
 * Cookie 관련 유틸리티 클래스
 */
public class CookieUtil {

    /**
     * Request에 있는 Cookie 중 name에 해당하는 값을 찾아 반환한다.
     *
     * @param request HttpServletRequest
     * @param name    찾을 Cookie 이름
     * @return Optional<String>
     */
    public static Optional<String> refineCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CommonException(ErrorCode.INVALID_COOKIE_ERROR);
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst().map(Cookie::getValue);
    }

    /**
     * Response에 Cookie를 추가한다.
     *
     * @param response     HttpServletResponse
     * @param cookieDomain Cookie 도메인
     * @param name         Cookie 이름
     * @param value        Cookie 값
     */
    public static void addCookie(HttpServletResponse response, String cookieDomain, String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(cookieDomain)
                .path("/")
                .httpOnly(true)
                .maxAge(60 * 60) // 1 hour
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Response에 Secure Cookie를 추가한다.
     *
     * @param response     HttpServletResponse
     * @param cookieDomain Cookie 도메인
     * @param name         Cookie 이름
     * @param value        Cookie 값
     * @param maxAge       Cookie 만료 시간
     */
    public static void addSecureCookie(HttpServletResponse response, String cookieDomain, String name, String value, Integer maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(cookieDomain)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Request에 있는 Cookie 중 name에 해당하는 값을 삭제한다.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param name     삭제할 Cookie 이름
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieDomain, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                ResponseCookie removedCookie = ResponseCookie.from(name, "")
                        .domain(cookieDomain)
                        .path("/")
                        .maxAge(0)
                        .httpOnly(true)
                        .build();
                response.addHeader("Set-Cookie", removedCookie.toString());
            }
        }
    }
}
