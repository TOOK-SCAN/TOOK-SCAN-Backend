package com.tookscan.tookscan.security.handler.common;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DefaultAccessDeniedHandler
        extends AbstractFailureHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        log.atWarn()
            .setCause(accessDeniedException)
            .addKeyValue("app.error.code", ErrorCode.ACCESS_DENIED.name())
            .addKeyValue("requested_url", request.getRequestURI())
            .addKeyValue("http_method", request.getMethod())
            .log("Access denied - Forbidden");
        setErrorResponse(response, ErrorCode.ACCESS_DENIED);
    }
}
