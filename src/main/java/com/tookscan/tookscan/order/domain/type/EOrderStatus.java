package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EOrderStatus {
    APPLY_COMPLETED("신청완료"),
    COMPANY_ARRIVED("업체도착"),
    PAYMENT_WAITING("결제대기"),
    PAYMENT_COMPLETED("결제완료"),
    SCAN_WAITING("스캔대기"),
    SCAN_IN_PROGRESS("스캔중"),
    RECOVERY_IN_PROGRESS("복원작업"),
    POST_WAITING("발송대기"),
    ALL_COMPLETED("작업완료"),
    CANCEL("취소접수"),
    AS("A/S 접수");

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

    /**
     * 사용자에게 표시할 상태로 변환
     *
     * @return 사용자에게 표시할 상태 문자열
     */
    public EOrderStatus toDisplayString() {
        return switch (this) {
            case APPLY_COMPLETED, COMPANY_ARRIVED -> APPLY_COMPLETED;
            case PAYMENT_WAITING -> PAYMENT_WAITING;
            case PAYMENT_COMPLETED -> PAYMENT_COMPLETED;
            case SCAN_WAITING, SCAN_IN_PROGRESS -> SCAN_IN_PROGRESS;
            case RECOVERY_IN_PROGRESS, POST_WAITING, ALL_COMPLETED -> ALL_COMPLETED;
            case CANCEL -> CANCEL;
            case AS -> AS;
        };
    }

    public String toDisplayScanStatusString() {
        return switch (this) {
            case APPLY_COMPLETED, COMPANY_ARRIVED, PAYMENT_WAITING, PAYMENT_COMPLETED, SCAN_WAITING -> "스캔대기";
            case SCAN_IN_PROGRESS -> "스캔중";
            case RECOVERY_IN_PROGRESS, POST_WAITING, ALL_COMPLETED -> "스캔완료";
            case CANCEL -> "취소";
            case AS -> "A/S";
        };
    }

    public static List<EOrderStatus> getScanStatusList(EScanStatus scanStatus) {
        return switch (scanStatus) {
            case WAITING -> List.of(APPLY_COMPLETED, COMPANY_ARRIVED, PAYMENT_WAITING);
            case IN_PROGRESS -> List.of(SCAN_WAITING, SCAN_IN_PROGRESS, PAYMENT_COMPLETED);
            case COMPLETED -> List.of(RECOVERY_IN_PROGRESS, POST_WAITING, ALL_COMPLETED);
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }
}

