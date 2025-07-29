package com.tookscan.tookscan.core.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Component;

/**
 * Spring Boot 3.4 Structured Logging 유틸리티
 */
@Component
public class StructuredLoggerUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static StructuredLogBuilder info(Logger logger) {
        return new StructuredLogBuilder(logger.atInfo());
    }

    public static StructuredLogBuilder warn(Logger logger) {
        return new StructuredLogBuilder(logger.atWarn());
    }

    public static StructuredLogBuilder error(Logger logger) {
        return new StructuredLogBuilder(logger.atError());
    }

    public static StructuredLogBuilder debug(Logger logger) {
        return new StructuredLogBuilder(logger.atDebug());
    }

    public static class StructuredLogBuilder {
        private final LoggingEventBuilder eventBuilder;
        private String message;
        
        public StructuredLogBuilder(LoggingEventBuilder eventBuilder) {
            this.eventBuilder = eventBuilder;
        }

        public StructuredLogBuilder message(String message) {
            this.message = message;
            return this;
        }

        public StructuredLogBuilder field(String key, Object value) {
            if (value != null) {
                eventBuilder.addKeyValue(key, value);
            }
            return this;
        }

        public StructuredLogBuilder fields(Map<String, Object> fields) {
            if (fields != null) {
                fields.forEach((key, value) -> {
                    if (value != null) {
                        eventBuilder.addKeyValue(key, value);
                    }
                });
            }
            return this;
        }

        public StructuredLogBuilder httpResponse(int statusCode, long durationMs) {
            Map<String, Object> responseDetails = new HashMap<>();
            responseDetails.put("status_code", statusCode);
            responseDetails.put("duration_ms", durationMs);
            return field("http_response", responseDetails);
        }

        public StructuredLogBuilder exception(Throwable throwable) {
            if (throwable != null) {
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("type", throwable.getClass().getSimpleName());
                errorDetails.put("message", throwable.getMessage());
                eventBuilder.setCause(throwable);
                return field("error", errorDetails);
            }
            return this;
        }

        public void log() {
            eventBuilder.log(message);
        }

    }

    public static class MDCUtil {
        public static void setupContext(HttpServletRequest request) {
            MDC.put("trace_id", UUID.randomUUID().toString());

            Map<String, Object> requestDetails = new HashMap<>();
            requestDetails.put("method", request.getMethod());
            requestDetails.put("uri", request.getRequestURI());
            MDC.put("http_request", safeWriteValueAsString(requestDetails));

            String clientIp = request.getHeader("X-FORWARDED-FOR") != null ?
                    request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
            Map<String, String> client = new HashMap<>();
            client.put("ip", clientIp);
            MDC.put("client", safeWriteValueAsString(client));
        }

        public static void setUserContext(String userId, String userRole) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", userId);
            user.put("roles", Collections.singletonList(userRole));
            MDC.put("user", safeWriteValueAsString(user));
        }

        public static void clear() {
            MDC.clear();
        }

        private static String safeWriteValueAsString(Object value) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize object to JSON string", e);
            }
        }
    }
}