package com.tookscan.tookscan.payment.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EEasyPaymentProvider {
    TOSS_PAY("토스페이"),
    NAVER_PAY("네이버페이"),
    SAMSUNG_PAY("삼성페이"),
    APPLE_PAY("애플페이"),
    L_PAY("엘페이"),
    KAKAO_PAY("카카오페이"),
    PIN_PAY("핀페이"),
    PAYCO("페이코"),
    SSG_PAY("SSG페이");

    private final String name;

    public static EEasyPaymentProvider fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        }
        return switch (value.toUpperCase()) {
            case "토스페이" -> TOSS_PAY;
            case "네이버페이" -> NAVER_PAY;
            case "삼성페이" -> SAMSUNG_PAY;
            case "애플페이" -> APPLE_PAY;
            case "엘페이" -> L_PAY;
            case "카카오페이" -> KAKAO_PAY;
            case "핀페이" -> PIN_PAY;
            case "페이코" -> PAYCO;
            case "SSG페이" -> SSG_PAY;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }

}
