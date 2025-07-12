package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ERecoveryOption {
    DISCARD("폐기", 0),
    RAW("원본", 0),
    SPRING("스프링", 3000);

    private final String description;
    private final Integer price;

    public static ERecoveryOption fromString(String value) {
        return switch (value.toUpperCase()) {
            case "DISCARD" -> DISCARD;
            case "RAW" -> RAW;
            case "SPRING" -> SPRING;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }
}
