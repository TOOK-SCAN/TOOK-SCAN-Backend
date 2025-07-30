package com.tookscan.tookscan.core.aspect;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class BusinessLogAspect {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, Logger> loggerCache = new ConcurrentHashMap<>();

    @Around("@annotation(businessLog)")
    public Object logBusinessProcess(ProceedingJoinPoint joinPoint, BusinessLog businessLog) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger logger = getLogger(targetClass);
        long startTime = System.currentTimeMillis();

        try {
            logStart(joinPoint, businessLog, logger);
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logEnd(joinPoint, businessLog, logger, result, executionTime, null);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logEnd(joinPoint, businessLog, logger, null, executionTime, e);
            throw e;
        }
    }

    private void logStart(ProceedingJoinPoint joinPoint, BusinessLog businessLog, Logger logger) {
        try {
            EvaluationContext context = createEvaluationContext(joinPoint, businessLog, null);
            String startMessage = parseExpression(businessLog.startMessage(), context);
            Map<String, Object> startDetails = parseDetailsExpressions(businessLog.startDetails(), context);
            logWithLevel(businessLog.level(), logger, startMessage, startDetails, null);
        } catch (Exception e) {
            log.warn("Failed to log business process start for method: {}", joinPoint.getSignature().getName(), e);
        }
    }

    private void logEnd(ProceedingJoinPoint joinPoint, BusinessLog businessLog, Logger logger,
                       Object result, long executionTime, Exception exception) {
        try {
            EvaluationContext context = createEvaluationContext(joinPoint, businessLog, result);
            String endMessage;
            Map<String, Object> endDetails = new LinkedHashMap<>();

            if (exception != null) {
                endMessage = parseExpression("[#{#domain}] #{#userType} #{#action} failed with exception", context);
            } else {
                endMessage = parseExpression(businessLog.endMessage(), context);
                endDetails = parseDetailsExpressions(businessLog.endDetails(), context);
            }

            if (businessLog.includeExecutionTime()) {
                endDetails.put("execution_time_ms", executionTime);
            }

            if (exception != null) {
                logWithLevel(BusinessLog.LogLevel.ERROR, logger, endMessage, endDetails, exception);
            } else {
                logWithLevel(businessLog.level(), logger, endMessage, endDetails, null);
            }
        } catch (Exception e) {
            log.warn("Failed to log business process end for method: {}", joinPoint.getSignature().getName(), e);
        }
    }

    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint,
                                                     BusinessLog businessLog, Object result) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("domain", businessLog.domain());
        context.setVariable("action", businessLog.action());
        context.setVariable("userType", businessLog.userType());

        Object[] args = joinPoint.getArgs();
        context.setVariable("args", args);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length && i < args.length; i++) {
            String paramName = parameters[i].getName();
            context.setVariable(paramName, args[i]);
        }

        if (result != null) {
            context.setVariable("result", result);
            String resultClassName = result.getClass().getSimpleName().toLowerCase();
            context.setVariable(resultClassName, result);
        }
        return context;
    }

    private String parseExpression(String expressionString, EvaluationContext context) {
        if (!StringUtils.hasText(expressionString)) {
            return "";
        }
        try {
            Expression expression = expressionCache.computeIfAbsent(expressionString,
                key -> parser.parseExpression(key, ParserContext.TEMPLATE_EXPRESSION));
            Object value = expression.getValue(context);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            log.warn("Failed to parse SpEL expression: {}", expressionString, e);
            return expressionString;
        }
    }

    private Map<String, Object> parseDetailsExpressions(String[] detailExpressions, EvaluationContext context) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (String detailExpression : detailExpressions) {
            if (!StringUtils.hasText(detailExpression)) {
                continue;
            }
            try {
                String[] parts = detailExpression.split(":", 2);
                if (parts.length != 2) {
                    log.warn("Invalid detail expression format: {}. Expected 'key: expression'", detailExpression);
                    continue;
                }
                String key = parts[0].trim().replaceAll("[\"']", "");
                String expressionString = parts[1].trim();

                Expression expression = expressionCache.computeIfAbsent(expressionString,
                    expKey -> parser.parseExpression(expKey));
                Object value = expression.getValue(context);

                if (value != null) {
                    details.put(key, value);
                }
            } catch (Exception e) {
                log.warn("Failed to parse detail expression: {}", detailExpression, e);
            }
        }
        return details;
    }

    private void logWithLevel(BusinessLog.LogLevel level, Logger logger, String message, Map<String, Object> details, Throwable throwable) {
        LoggingEventBuilder builder = switch (level) {
            case DEBUG -> logger.atDebug();
            case INFO -> logger.atInfo();
            case WARN -> logger.atWarn();
            case ERROR -> logger.atError();
        };

        if (details != null && !details.isEmpty()) {
            builder.addKeyValue("details", details);
        }

        if (throwable != null) {
            builder.setCause(throwable);
        }

        builder.log(message);
    }

    private Logger getLogger(Class<?> clazz) {
        return loggerCache.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }
}
