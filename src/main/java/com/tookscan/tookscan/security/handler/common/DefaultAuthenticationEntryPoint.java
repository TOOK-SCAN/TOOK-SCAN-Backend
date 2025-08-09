package com.tookscan.tookscan.security.handler.common;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DefaultAuthenticationEntryPoint
        extends AbstractFailureHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = request.getAttribute("exception") == null ?
                ErrorCode.NOT_FOUND_END_POINT : (ErrorCode) request.getAttribute("exception");

        log.atWarn()
            .setCause(authException)
            .addKeyValue("app.error.code", errorCode.name())
            .addKeyValue("requested_url", request.getRequestURI())
            .addKeyValue("http_method", request.getMethod())
            .log("Authentication failed - Unauthorized");

        setErrorResponse(response, errorCode);
    }
}
