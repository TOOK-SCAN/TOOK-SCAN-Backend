package com.tookscan.tookscan.security.handler.logout;

import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.utility.CookieUtil;
import com.tookscan.tookscan.core.utility.HttpServletUtil;
import com.tookscan.tookscan.security.handler.common.AbstractFailureHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DefaultLogoutSuccessHandler
        extends AbstractFailureHandler implements LogoutSuccessHandler {

    private final HttpServletUtil httpServletUtil;

    @Value("${web-engine.cookie-domain}")
    private String cookieDomain;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (authentication == null) {
            setErrorResponse(response, refineErrorCode(request));
            return;
        }


        CookieUtil.deleteCookie(request, response, cookieDomain, Constants.ACCESS_TOKEN);
        CookieUtil.deleteCookie(request, response, cookieDomain, Constants.REFRESH_TOKEN);
        CookieUtil.deleteCookie(request, response, cookieDomain, Constants.TEMPORARY_TOKEN);
        CookieUtil.deleteCookie(request, response, cookieDomain, "JSESSIONID");


        httpServletUtil.onSuccessBodyResponse(response, HttpStatus.OK);
    }

    private ErrorCode refineErrorCode(HttpServletRequest request) {
        if (request.getAttribute("exception") == null) {
            return ErrorCode.INTERNAL_SERVER_ERROR;
        }

        return (ErrorCode) request.getAttribute("exception");
    }
}
