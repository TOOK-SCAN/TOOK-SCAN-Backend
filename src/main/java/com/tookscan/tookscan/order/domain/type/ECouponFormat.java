package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ECouponFormat {
    DEFINED("코드 지정"),
    AUTO_GENERATED("자동 발행")

    ;
    private final String description;

    public static ECouponFormat fromString(String value) {
        return switch (value.toUpperCase()) {
            case "DEFINED" -> DEFINED;
            case "AUTO_GENERATED" -> AUTO_GENERATED;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }
}
