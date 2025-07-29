package com.tookscan.tookscan.security.filter;

import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil.MDCUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class GlobalLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            MDCUtil.setupContext(request);

            StructuredLoggerUtil.info(log)
                    .message("[Global] HTTP Request Received")
                    .log();

            request.setAttribute("INTERCEPTOR_PRE_HANDLE_TIME", System.currentTimeMillis());

            filterChain.doFilter(request, response);

            Long preHandleTime = (Long) request.getAttribute("INTERCEPTOR_PRE_HANDLE_TIME");
            Long postHandleTime = System.currentTimeMillis();
            Long processingTime = postHandleTime - preHandleTime;

            StructuredLoggerUtil.info(log)
                    .message("[Global] HTTP Request Has Been Processed")
                    .httpResponse(response.getStatus(), processingTime)
                    .log();

        } finally {
            MDCUtil.clear();
        }
    }
}
