package com.tookscan.tookscan.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.exception.type.HttpSecurityException;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (HttpSecurityException e) {
            handleException(response, e, e.getErrorCode());
        } catch (CommonException e) {
            handleException(response, e, e.getErrorCode());
        } catch (SecurityException e) {
            handleException(response, e, ErrorCode.ACCESS_DENIED);
        } catch (Exception e) {
            handleException(response, e, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 예외 처리를 위한 통합 private 메서드
     *
     * @param response  HttpServletResponse
     * @param throwable 발생한 예외
     * @param errorCode 응답할 에러 코드
     */
    private void handleException(HttpServletResponse response, Throwable throwable,
                                 ErrorCode errorCode) throws IOException {
        // 1. 에러 로그 기록
        logError(throwable, errorCode);

        // 2. 에러 응답 전송
        sendErrorResponse(response, errorCode);
    }

    /**
     * 구조화된 에러 로그를 기록하는 역할
     */
    private void logError(Throwable throwable, ErrorCode errorCode) {
        StructuredLoggerUtil.error(log)
                .message("FilterException " + throwable.getClass().getSimpleName() + " occurred")
                .exception(throwable, errorCode)
                .log();
    }

    /**
     * 클라이언트에게 JSON 형태의 에러 응답을 보내는 역할
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", errorCode.getCode());
        errorDetails.put("message", errorCode.getMessage());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", null);
        responseBody.put("success", false);
        responseBody.put("error", errorDetails);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
