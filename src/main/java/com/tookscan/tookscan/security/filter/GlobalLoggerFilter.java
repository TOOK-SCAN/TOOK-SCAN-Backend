package com.tookscan.tookscan.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class GlobalLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            MDC.put("trace.id", UUID.randomUUID().toString());
            MDC.put("http.request.method", request.getMethod());
            MDC.put("url.path", request.getRequestURI());
            MDC.put("user_agent.original", request.getHeader("User-Agent"));
            MDC.put("client.ip", request.getRemoteAddr());

            log.atInfo().log("[Global] HTTP Request Received");

            request.setAttribute("INTERCEPTOR_PRE_HANDLE_TIME", System.currentTimeMillis());

            filterChain.doFilter(request, response);

            Long preHandleTime = (Long) request.getAttribute("INTERCEPTOR_PRE_HANDLE_TIME");
            Long postHandleTime = System.currentTimeMillis();
            Long processingTime = postHandleTime - preHandleTime;

            log.atInfo()
                .addKeyValue("http.response.status_code", response.getStatus())
                .addKeyValue("event.duration", processingTime * 1_000_000) // nanoseconds
                .log("[Global] HTTP Request Has Been Processed");

        } finally {
            MDC.clear();
        }
    }
}
