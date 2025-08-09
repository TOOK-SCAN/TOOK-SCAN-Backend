package com.tookscan.tookscan.security.handler.login;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.security.handler.common.AbstractFailureHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class Oauth2FailureHandler
        extends AbstractFailureHandler implements AuthenticationFailureHandler {

    private static final String BAD_CREDENTIALS_EXCEPTION = "BadCredentialsException";
    private static final String BAD_CREDENTIALS_MESSAGE = "잘못된 인증 정보입니다. 다시 시도해주세요.";

    private static final String INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION = "InternalAuthenticationServiceException";
    private static final String INTERNAL_AUTHENTICATION_SERVICE_MESSAGE = "인증 서비스에 문제가 발생했습니다. 관리자에게 문의하세요.";

    private static final String AUTHENTICATION_CREDENTIALS_NOT_FOUND_EXCEPTION = "AuthenticationCredentialsNotFoundException";
    private static final String AUTHENTICATION_CREDENTIALS_NOT_FOUND_MESSAGE = "필요한 인증 정보가 누락되었습니다. 다시 시도해주세요.";

    private static final String DEFAULT_MESSAGE = "OAuth2 인증 중 알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message = switch (exception.getClass().getSimpleName()) {
            case BAD_CREDENTIALS_EXCEPTION -> BAD_CREDENTIALS_MESSAGE;
            case INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION -> INTERNAL_AUTHENTICATION_SERVICE_MESSAGE;
            case AUTHENTICATION_CREDENTIALS_NOT_FOUND_EXCEPTION -> AUTHENTICATION_CREDENTIALS_NOT_FOUND_MESSAGE;
            default -> DEFAULT_MESSAGE;
        };

        log.atWarn()
            .setCause(exception)
            .addKeyValue("app.error.code", ErrorCode.FAILURE_LOGIN.name())
            .addKeyValue("requested_url", request.getRequestURI())
            .addKeyValue("http_method", request.getMethod())
            .log("OAuth2 login failed - Authentication error");

        setErrorResponse(
                response,
                message,
                ErrorCode.FAILURE_LOGIN
        );
    }
}
