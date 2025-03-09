package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ECouponType {
    DELIVERY_PRICE_FREE("배송비 무료"),
    PERCENTAGE("퍼센트"),
    AMOUNT("금액");

    private final String description;

    public static ECouponType fromString(String value) {
        return switch (value.toUpperCase()) {
            case "DELIVERY_PRICE_FREE" -> DELIVERY_PRICE_FREE;
            case "PERCENTAGE" -> PERCENTAGE;
            case "AMOUNT" -> AMOUNT;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }
}
