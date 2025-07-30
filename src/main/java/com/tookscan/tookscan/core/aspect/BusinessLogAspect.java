package com.tookscan.tookscan.core.aspect;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
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
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 비즈니스 로직 실행 로깅을 위한 AOP Aspect
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class BusinessLogAspect {
    
    private final ExpressionParser parser = new SpelExpressionParser();
    
    // 성능 최적화를 위한 SpEL 표현식 캐시
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
    
    // Logger 인스턴스 캐시
    private final Map<Class<?>, Logger> loggerCache = new ConcurrentHashMap<>();
    
    @Around("@annotation(businessLog)")
    public Object logBusinessProcess(ProceedingJoinPoint joinPoint, BusinessLog businessLog) throws Throwable {
        
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger logger = getLogger(targetClass);
        
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            // 시작 로그
            logStart(joinPoint, businessLog, logger);
            
            // 실제 메서드 실행
            Object result = joinPoint.proceed();
            
            // 종료 로그 (정상 완료)
            long executionTime = System.currentTimeMillis() - startTime;
            logEnd(joinPoint, businessLog, logger, result, executionTime, null);
            
            return result;
            
        } catch (Exception e) {
            // 종료 로그 (예외 발생)
            long executionTime = System.currentTimeMillis() - startTime;
            logEnd(joinPoint, businessLog, logger, null, executionTime, e);
            throw e;
        }
    }
    
    /**
     * 시작 로그 출력
     */
    private void logStart(ProceedingJoinPoint joinPoint, BusinessLog businessLog, Logger logger) {
        try {
            EvaluationContext context = createEvaluationContext(joinPoint, businessLog, null);
            
            // 시작 메시지 생성
            String startMessage = parseExpression(businessLog.startMessage(), context);
            
            // 시작 details 생성
            Map<String, Object> startDetails = parseDetailsExpressions(businessLog.startDetails(), context);
            
            // 로그 출력
            logWithLevel(businessLog.level(), logger, startMessage, startDetails);
            
        } catch (Exception e) {
            log.warn("Failed to log business process start for method: {}", joinPoint.getSignature().getName(), e);
        }
    }
    
    /**
     * 종료 로그 출력
     */
    private void logEnd(ProceedingJoinPoint joinPoint, BusinessLog businessLog, Logger logger, 
                       Object result, long executionTime, Exception exception) {
        try {
            EvaluationContext context = createEvaluationContext(joinPoint, businessLog, result);
            
            String endMessage;
            Map<String, Object> endDetails = new LinkedHashMap<>();
            
            if (exception != null) {
                // 예외 발생 시
                endMessage = parseExpression("[#{#domain}] #{#userType} #{#action} failed with exception", context);
                endDetails.put("error_type", exception.getClass().getSimpleName());
                endDetails.put("error_message", exception.getMessage());
            } else {
                // 정상 완료 시
                endMessage = parseExpression(businessLog.endMessage(), context);
                endDetails = parseDetailsExpressions(businessLog.endDetails(), context);
            }
            
            // 실행 시간 포함
            if (businessLog.includeExecutionTime()) {
                endDetails.put("execution_time_ms", executionTime);
            }
            
            // 로그 출력
            if (exception != null) {
                StructuredLoggerUtil.error(logger)
                    .message(endMessage)
                    .details(endDetails)
                    .exception(exception)
                    .log();
            } else {
                logWithLevel(businessLog.level(), logger, endMessage, endDetails);
            }
            
        } catch (Exception e) {
            log.warn("Failed to log business process end for method: {}", joinPoint.getSignature().getName(), e);
        }
    }
    
    /**
     * SpEL 평가 컨텍스트 생성
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint, 
                                                     BusinessLog businessLog, Object result) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // 기본 변수 설정
        context.setVariable("domain", businessLog.domain());
        context.setVariable("action", businessLog.action());
        context.setVariable("userType", businessLog.userType());
        
        // 메서드 파라미터 설정
        Object[] args = joinPoint.getArgs();
        context.setVariable("args", args);
        
        // 파라미터를 이름으로 접근 가능하도록 설정
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            String paramName = parameters[i].getName();
            context.setVariable(paramName, args[i]);
        }
        
        // 결과값 설정 (있는 경우)
        if (result != null) {
            context.setVariable("result", result);
            
            // 결과 타입에 따른 추가 변수 설정
            String resultClassName = result.getClass().getSimpleName().toLowerCase();
            context.setVariable(resultClassName, result);
        }
        
        return context;
    }
    
    /**
     * SpEL 표현식 파싱 및 평가 (템플릿 지원)
     */
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
            return expressionString; // fallback to literal string
        }
    }
    
    /**
     * Details 표현식들을 파싱하여 Map으로 변환
     */
    private Map<String, Object> parseDetailsExpressions(String[] detailExpressions, EvaluationContext context) {
        Map<String, Object> details = new LinkedHashMap<>();
        
        for (String detailExpression : detailExpressions) {
            if (!StringUtils.hasText(detailExpression)) {
                continue;
            }
            
            try {
                // "key": "expression" 형태 파싱
                String[] parts = detailExpression.split(":", 2);
                if (parts.length != 2) {
                    log.warn("Invalid detail expression format: {}. Expected 'key: expression'", detailExpression);
                    continue;
                }
                
                String key = parts[0].trim().replaceAll("[\"{'}]", "");
                String expressionString = parts[1].trim().replaceAll("[\"{'}]", "");
                
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
    
    /**
     * 로그 레벨에 따른 로그 출력
     */
    private void logWithLevel(BusinessLog.LogLevel level, Logger logger, String message, Map<String, Object> details) {
        switch (level) {
            case DEBUG -> StructuredLoggerUtil.debug(logger).message(message).details(details).log();
            case INFO -> StructuredLoggerUtil.info(logger).message(message).details(details).log();
            case WARN -> StructuredLoggerUtil.warn(logger).message(message).details(details).log();
            case ERROR -> StructuredLoggerUtil.error(logger).message(message).details(details).log();
        }
    }
    
    /**
     * Logger 인스턴스 캐시에서 가져오기
     */
    private Logger getLogger(Class<?> clazz) {
        return loggerCache.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }
}