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

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class HttpGlobalExceptionHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    // Convertor 에서 바인딩 실패시 발생하는 예외
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseDto<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("ExceptionHandler catch HttpMessageNotReadableException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.BAD_REQUEST_JSON));
    }

    // 지원되지 않는 미디어 타입을 사용할 때 발생하는 예외
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseDto<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("ExceptionHandler catch HttpMediaTypeNotSupportedException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseDto<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("ExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseDto<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("ExceptionHandler catch HttpRequestMethodNotSupportedException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // Body Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("ExceptionHandler catch MethodArgumentNotValidException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // Annotation Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public ResponseDto<?> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error("ExceptionHandler catch HandlerMethodValidationException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // Constraint Validation 에서 검증 실패시 발생하는 예외
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseDto<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ExceptionHandler catch ConstraintViolationException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {UnexpectedTypeException.class})
    public ResponseDto<?> handleUnexpectedTypeException(UnexpectedTypeException e) {
        log.error("ExceptionHandler catch UnexpectedTypeException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 메소드의 인자 타입이 일치하지 않을 때 발생하는 예외
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentTypeMismatchException e) {
        log.error("ExceptionHandler catch MethodArgumentTypeMismatchException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 필수 파라미터가 누락되었을 때 발생하는 예외
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseDto<?> handleArgumentNotValidException(MissingServletRequestParameterException e) {
        log.error("ExceptionHandler catch MissingServletRequestParameterException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {HttpSecurityException.class})
    public ResponseDto<?> handleApiException(HttpSecurityException e) {
        log.error("ExceptionHandler catch HttpJsonWebTokenException : {}", e.getMessage());
        return ResponseDto.fail(e);
    }

    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleApiException(CommonException e) {
        log.error("ExceptionHandler catch HttpCommonException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 타입이 잘못되었을 때 발생하는 예외
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseDto<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("ExceptionHandler catch IllegalArgumentException : {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(e);
    }

    // 외부 서버 연결 타임아웃 예외
    @ExceptionHandler(value = {SocketTimeoutException.class})
    public ResponseDto<?> handleSocketTimeoutException(SocketTimeoutException e) {
        log.error("SocketTimeoutException occurred: {}", e.getMessage());
        sendSlackEvent(e);
        return ResponseDto.fail(new CommonException(ErrorCode.EXTERNAL_SERVER_ERROR, "타임아웃이 발생했습니다."));
    }

    // 서버, DB 예외
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleException(Exception e) {
        log.error("ExceptionHandler catch Exception : {}", e.getMessage());
        e.printStackTrace();
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
