package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EOrderStatus {
    APPLY_COMPLETED("신청완료"),
    COMPANY_ARRIVED("회사도착"),
    PAYMENT_WAITING("결제대기"),
    PAYMENT_COMPLETED("결제완료"),
    SCAN_WAITING("스캔대기"),
    SCAN_IN_PROGRESS("스캔진행중"),
    RECOVERY_IN_PROGRESS("회수진행중"),
    POST_WAITING("배송대기"),
    ALL_COMPLETED("전체완료"),
    CANCEL("취소"),
    AS("AS");

    private final String description;

    public static EOrderStatus fromString(String value) {
        return switch (value.toUpperCase()) {
            case "APPLY_COMPLETED" -> APPLY_COMPLETED;
            case "COMPANY_ARRIVED" -> COMPANY_ARRIVED;
            case "PAYMENT_WAITING" -> PAYMENT_WAITING;
            case "PAYMENT_COMPLETED" -> PAYMENT_COMPLETED;
            case "SCAN_WAITING" -> SCAN_WAITING;
            case "SCAN_IN_PROGRESS" -> SCAN_IN_PROGRESS;
            case "RECOVERY_IN_PROGRESS" -> RECOVERY_IN_PROGRESS;
            case "POST_WAITING" -> POST_WAITING;
            case "ALL_COMPLETED" -> ALL_COMPLETED;
            case "CANCEL" -> CANCEL;
            case "AS" -> AS;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }
}
