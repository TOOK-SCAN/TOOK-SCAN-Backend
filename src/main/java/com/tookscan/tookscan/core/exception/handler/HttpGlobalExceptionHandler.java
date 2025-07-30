package com.tookscan.tookscan.core.exception.handler;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.dto.SendSlackErrorDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.exception.type.HttpSecurityException;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import java.net.SocketTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HttpGlobalExceptionHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    // Convertor 에서 바인딩 실패시 발생하는 예외
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseDto<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        StructuredLoggerUtil.warn(log)
                .message("HTTP 메시지 읽기 실패 - 비정상적인 요청 데이터")
                .exception(e, ErrorCode.BAD_REQUEST_JSON)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.BAD_REQUEST_JSON));
    }

    // 지원되지 않는 미디어 타입을 사용할 때 발생하는 예외
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseDto<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        StructuredLoggerUtil.warn(log)
                .message("지원되지 않는 미디어 타입 사용")
                .field("supported_types", e.getSupportedMediaTypes())
                .exception(e, ErrorCode.UNSUPPORTED_MEDIA_TYPE)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseDto<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        StructuredLoggerUtil.warn(log)
                .message("존재하지 않는 엔드포인트 요청")
                .field("requested_url", e.getRequestURL())
                .field("http_method", e.getHttpMethod())
                .exception(e, ErrorCode.METHOD_NOT_ALLOWED)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseDto<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        StructuredLoggerUtil.warn(log)
                .message("지원되지 않는 HTTP 메소드 사용")
                .field("requested_method", e.getMethod())
                .field("supported_methods", e.getSupportedMethods())
                .exception(e, ErrorCode.METHOD_NOT_ALLOWED)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // Body Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("요청에 유효하지 않은 인자입니다.");

        StructuredLoggerUtil.warn(log)
                .message("요청 데이터 유효성 검증 실패 - 비정상적인 입력값")
                .field("validation_message", message)
                .field("field_errors", e.getFieldErrorCount())
                .field("global_errors", e.getGlobalErrorCount())
                .exception(e, ErrorCode.INVALID_ARGUMENT)
                .log();
        sendSlackEvent(e);

        return ResponseDto.fail(new CommonException(ErrorCode.INVALID_ARGUMENT, message, true));
    }


    // Annotation Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public ResponseDto<?> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        StructuredLoggerUtil.warn(log)
                .message("메소드 매개변수 유효성 검증 실패 - 비정상적인 입력값")
                .exception(e)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // Constraint Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseDto<?> handleConstraintViolationException(ConstraintViolationException e) {
        StructuredLoggerUtil.warn(log)
                .message("제약 조건 위반 - 비정상적인 입력값")
                .field("violation_count", e.getConstraintViolations().size())
                .exception(e)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {UnexpectedTypeException.class})
    public ResponseDto<?> handleUnexpectedTypeException(UnexpectedTypeException e) {
        StructuredLoggerUtil.warn(log)
                .message("예상치 못한 타입 사용 - 비정상적인 입력값")
                .exception(e)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 메소드의 인자 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentTypeMismatchException e) {
        StructuredLoggerUtil.warn(log)
                .message("메소드 인자 타입 불일치 - 비정상적인 입력값")
                .field("parameter_name", e.getName())
                .field("invalid_value", e.getValue())
                .field("required_type", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown")
                .exception(e)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 필수 파라미터가 누락되었을 때 발생하는 예외
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseDto<?> handleArgumentNotValidException(MissingServletRequestParameterException e) {
        StructuredLoggerUtil.warn(log)
                .message("필수 요청 파라미터 누락 - 비정상적인 입력값")
                .field("parameter_name", e.getParameterName())
                .field("parameter_type", e.getParameterType())
                .exception(e)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {HttpSecurityException.class})
    public ResponseDto<?> handleApiException(HttpSecurityException e) {
        StructuredLoggerUtil.error(log)
                .message("보안 예외 발생 - 요청 처리 실패")
                .exception(e, e.getErrorCode())
                .log();
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleApiException(CommonException e) {
        StructuredLoggerUtil.error(log)
                .message("비즈니스 로직 예외 발생 - 요청 처리 실패")
                .exception(e, e.getErrorCode())
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 타입이 잘못되었을 때 발생하는 예외
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseDto<?> handleIllegalArgumentException(IllegalArgumentException e) {
        StructuredLoggerUtil.warn(log)
                .message("비정상적인 인자 전달 - 비정상적인 입력값")
                .exception(e)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 외부 서버 연결 타임아웃 예외
    @ExceptionHandler(value = {SocketTimeoutException.class})
    public ResponseDto<?> handleSocketTimeoutException(SocketTimeoutException e) {
        StructuredLoggerUtil.error(log)
                .message("외부 서버 연결 타임아웃 - 요청 처리 실패")
                .exception(e, ErrorCode.EXTERNAL_SERVER_ERROR)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.EXTERNAL_SERVER_ERROR, "타임아웃이 발생했습니다."));
    }

    // 서버, DB 예외
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleException(Exception e) {
        StructuredLoggerUtil.error(log)
                .message("예상치 못한 시스템 오류 발생 - 요청 처리 실패")
                .exception(e, ErrorCode.INTERNAL_SERVER_ERROR)
                .log();
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /* -------------------------------------------- */
    /* Private Method ----------------------------- */
    /* -------------------------------------------- */
    private void sendSlackEvent(Exception e) {
        applicationEventPublisher.publishEvent(
                SendSlackErrorDto.of(
                        e
                )
        );
    }
}
