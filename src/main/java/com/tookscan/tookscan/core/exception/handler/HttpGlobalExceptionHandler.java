package com.tookscan.tookscan.core.exception.handler;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.dto.SendSlackErrorDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.exception.type.HttpSecurityException;
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
        log.atWarn()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.BAD_REQUEST_JSON.name())
            .log("HTTP 메시지 읽기 실패 - 비정상적인 요청 데이터");
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.BAD_REQUEST_JSON));
    }

    // 지원되지 않는 미디어 타입을 사용할 때 발생하는 예외
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseDto<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.atWarn()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.UNSUPPORTED_MEDIA_TYPE.name())
            .addKeyValue("supported_types", e.getSupportedMediaTypes())
            .log("지원되지 않는 미디어 타입 사용");
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseDto<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.atWarn()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.METHOD_NOT_ALLOWED.name())
            .addKeyValue("requested_url", e.getRequestURL())
            .addKeyValue("http_method", e.getHttpMethod())
            .log("존재하지 않는 엔드포인트 요청");
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseDto<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.atWarn()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.METHOD_NOT_ALLOWED.name())
            .addKeyValue("requested_method", e.getMethod())
            .addKeyValue("supported_methods", e.getSupportedMethods())
            .log("지원되지 않는 HTTP 메소드 사용");
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

        log.atWarn()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.INVALID_ARGUMENT.name())
            .addKeyValue("validation_message", message)
            .addKeyValue("field_errors", e.getFieldErrorCount())
            .addKeyValue("global_errors", e.getGlobalErrorCount())
            .log("요청 데이터 유효성 검증 실패 - 비정상적인 입력값");
        sendSlackEvent(e);

        return ResponseDto.fail(new CommonException(ErrorCode.INVALID_ARGUMENT, message, true));
    }


    // Annotation Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public ResponseDto<?> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.atWarn()
            .setCause(e)
            .log("메소드 매개변수 유효성 검증 실패 - 비정상적인 입력값");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // Constraint Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseDto<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.atWarn()
            .setCause(e)
            .addKeyValue("violation_count", e.getConstraintViolations().size())
            .log("제약 조건 위반 - 비정상적인 입력값");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {UnexpectedTypeException.class})
    public ResponseDto<?> handleUnexpectedTypeException(UnexpectedTypeException e) {
        log.atWarn()
            .setCause(e)
            .log("예상치 못한 타입 사용 - 비정상적인 입력값");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 메소드의 인자 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentTypeMismatchException e) {
        log.atWarn()
            .setCause(e)
            .addKeyValue("parameter_name", e.getName())
            .addKeyValue("invalid_value", e.getValue())
            .addKeyValue("required_type", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown")
            .log("메소드 인자 타입 불일치 - 비정상적인 입력값");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 필수 파라미터가 누락되었을 때 발생하는 예외
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseDto<?> handleArgumentNotValidException(MissingServletRequestParameterException e) {
        log.atWarn()
            .setCause(e)
            .addKeyValue("parameter_name", e.getParameterName())
            .addKeyValue("parameter_type", e.getParameterType())
            .log("필수 요청 파라미터 누락 - 비정상적인 입력값");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {HttpSecurityException.class})
    public ResponseDto<?> handleApiException(HttpSecurityException e) {
        log.atError()
            .setCause(e)
            .addKeyValue("error.code", e.getErrorCode().name())
            .log("보안 예외 발생 - 요청 처리 실패");
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleApiException(CommonException e) {
        log.atError()
            .setCause(e)
            .addKeyValue("error.code", e.getErrorCode().name())
            .log("비즈니스 로직 예외 발생 - 요청 처리 실패");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 타입이 잘못되었을 때 발생하는 예외
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseDto<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.atWarn()
            .setCause(e)
            .log("비정상적인 인자 전달 - 비정상적인 입력값");
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 외부 서버 연결 타임아웃 예외
    @ExceptionHandler(value = {SocketTimeoutException.class})
    public ResponseDto<?> handleSocketTimeoutException(SocketTimeoutException e) {
        log.atError()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.EXTERNAL_SERVER_ERROR.name())
            .log("외부 서버 연결 타임아웃 - 요청 처리 실패");
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.EXTERNAL_SERVER_ERROR, "타임아웃이 발생했습니다."));
    }

    // 서버, DB 예외
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleException(Exception e) {
        log.atError()
            .setCause(e)
            .addKeyValue("error.code", ErrorCode.INTERNAL_SERVER_ERROR.name())
            .log("예상치 못한 시스템 오류 발생 - 요청 처리 실패");
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
