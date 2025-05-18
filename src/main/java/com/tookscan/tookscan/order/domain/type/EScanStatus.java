package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EScanStatus {
    WAITING("스캔대기"),
    UNABLE("스캔불가"),
    ENABLE("스캔가능"),
    IN_PROGRESS("스캔중"),
    COMPLETED("스캔완료"),
    FAILED("스캔실패");

    private final String description;

    public static EScanStatus fromString(String value) {
        return switch (value.toUpperCase()) {
            case "WAITING" -> WAITING;
            case "UNABLE" -> UNABLE;
            case "ENABLE" -> ENABLE;
            case "IN_PROGRESS" -> IN_PROGRESS;
            case "COMPLETED" -> COMPLETED;
            case "FAILED" -> FAILED;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }
}
