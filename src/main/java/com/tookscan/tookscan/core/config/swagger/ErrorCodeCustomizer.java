package com.tookscan.tookscan.core.config.swagger;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.annotation.swagger.ApiErrorExceptions;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import java.net.SocketTimeoutException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 에러 코드 예시를 Swagger 문서에 자동으로 추가하는 커스터마이저
 * 
 * @ApiErrorCode와 @ApiErrorExceptions 어노테이션이 적용된 API 메서드에 대해
 * 해당 에러 코드들의 상세한 응답 예시를 Swagger UI에 표시합니다.
 */
@Component
@RequiredArgsConstructor
public class ErrorCodeCustomizer implements OperationCustomizer {
    
    private static final String APPLICATION_JSON = "application/json";
    
    /**
     * 일반적인 예외와 에러 코드 매핑 (GlobalExceptionHandler 기반)
     */
    private static final Map<Class<? extends Exception>, ErrorCode> EXCEPTION_ERROR_CODE_MAP = Map.ofEntries(
        // Body Validation 검증 실패
        Map.entry(MethodArgumentNotValidException.class, ErrorCode.INVALID_ARGUMENT),
        
        // 필수 파라미터 누락
        Map.entry(MissingServletRequestParameterException.class, ErrorCode.MISSING_REQUEST_PARAMETER),
        
        // 메소드 인자 타입 불일치
        Map.entry(MethodArgumentTypeMismatchException.class, ErrorCode.INVALID_PARAMETER_FORMAT),
        
        // Annotation Validation 검증 실패
        Map.entry(HandlerMethodValidationException.class, ErrorCode.INVALID_ARGUMENT),
        
        // Constraint Validation 검증 실패
        Map.entry(ConstraintViolationException.class, ErrorCode.INVALID_ARGUMENT),
        
        // 타입 불일치
        Map.entry(UnexpectedTypeException.class, ErrorCode.INVALID_ARGUMENT),
        
        // JSON 변환 실패
        Map.entry(HttpMessageNotReadableException.class, ErrorCode.BAD_REQUEST_JSON),
        
        // 지원되지 않는 미디어 타입
        Map.entry(HttpMediaTypeNotSupportedException.class, ErrorCode.UNSUPPORTED_MEDIA_TYPE),
        
        // 지원되지 않는 HTTP 메소드
        Map.entry(HttpRequestMethodNotSupportedException.class, ErrorCode.METHOD_NOT_ALLOWED),
        
        // 존재하지 않는 엔드포인트
        Map.entry(NoHandlerFoundException.class, ErrorCode.NOT_FOUND_END_POINT),
        
        // 잘못된 인자
        Map.entry(IllegalArgumentException.class, ErrorCode.INVALID_ARGUMENT),
        
        // 외부 서버 타임아웃
        Map.entry(SocketTimeoutException.class, ErrorCode.EXTERNAL_SERVER_TIMEOUT),
        
        // 일반적인 서버 에러
        Map.entry(Exception.class, ErrorCode.INTERNAL_SERVER_ERROR)
    );
    
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiErrorCode apiErrorCode = handlerMethod.getMethodAnnotation(ApiErrorCode.class);
        ApiErrorExceptions apiErrorExceptions = handlerMethod.getMethodAnnotation(ApiErrorExceptions.class);
        
        if (apiErrorCode != null) {
            generateErrorCodeExamples(operation, apiErrorCode.value());
        }
        
        if (apiErrorExceptions != null) {
            generateExceptionExamples(operation, apiErrorExceptions.value());
        }
        
        return operation;
    }
    
    /**
     * ErrorCode 배열을 기반으로 에러 응답을 생성합니다.
     */
    private void generateErrorCodeExamples(Operation operation, ErrorCode[] errorCodes) {
        ApiResponses responses = operation.getResponses();
        
        Arrays.stream(errorCodes)
            .forEach(errorCode -> {
                String statusCode = String.valueOf(errorCode.getHttpStatus().value());
                
                // 에러 응답 생성
                Map<String, Object> errorResponse = createErrorResponse(errorCode);
                Example example = new Example();
                example.setValue(errorResponse);
                example.setSummary(errorCode.getMessage());
                example.setDescription(String.format("[%d] %s", errorCode.getCode(), errorCode.getMessage()));
                
                // 기존 응답이 있는지 확인하고 없으면 새로 생성
                ApiResponse apiResponse = responses.get(statusCode);
                if (apiResponse == null) {
                    apiResponse = new ApiResponse();
                    apiResponse.setDescription(errorCode.getHttpStatus().getReasonPhrase());
                    responses.put(statusCode, apiResponse);
                }
                
                // Content와 MediaType 설정
                Content content = apiResponse.getContent();
                if (content == null) {
                    content = new Content();
                    apiResponse.setContent(content);
                }
                
                MediaType mediaType = content.get(APPLICATION_JSON);
                if (mediaType == null) {
                    mediaType = new MediaType();
                    content.addMediaType(APPLICATION_JSON, mediaType);
                }
                
                // 예시 추가
                if (mediaType.getExamples() == null) {
                    mediaType.setExamples(new HashMap<>());
                }
                
                String exampleKey = String.format("error_%d", errorCode.getCode());
                mediaType.getExamples().put(exampleKey, example);
            });
    }
    
    /**
     * Exception 클래스 배열을 기반으로 에러 응답을 생성합니다.
     */
    private void generateExceptionExamples(Operation operation, Class<? extends Exception>[] exceptionClasses) {
        List<ErrorCode> errorCodes = Arrays.stream(exceptionClasses)
            .map(EXCEPTION_ERROR_CODE_MAP::get)
            .filter(errorCode -> errorCode != null)
            .toList();
        
        if (!errorCodes.isEmpty()) {
            generateErrorCodeExamples(operation, errorCodes.toArray(new ErrorCode[0]));
        }
    }
    
    /**
     * ErrorCode를 기반으로 표준 에러 응답 구조를 생성합니다.
     */
    private Map<String, Object> createErrorResponse(ErrorCode errorCode) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("code", errorCode.getCode());
        errorData.put("message", errorCode.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("data", null);
        response.put("error", errorData);
        
        return response;
    }
}