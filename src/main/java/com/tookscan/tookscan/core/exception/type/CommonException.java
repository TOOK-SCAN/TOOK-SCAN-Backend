package com.tookscan.tookscan.core.exception.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public CommonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public CommonException(ErrorCode errorCode, String customMessage, boolean override) {
        super(override ? customMessage : errorCode.getMessage() + " " + customMessage);
        this.errorCode = errorCode;
        this.message = override ? customMessage : errorCode.getMessage() + " " + customMessage;
    }

    // 커스텀 메시지만 전달 시 기본은 override = false
    public CommonException(ErrorCode errorCode, String customMessage) {
        this(errorCode, customMessage, false);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static CommonException of(ErrorCode errorCode, String message) {
        return new CommonException(errorCode, message);
    }

    public static CommonException of(ErrorCode errorCode) {
        return new CommonException(errorCode);
    }
}
