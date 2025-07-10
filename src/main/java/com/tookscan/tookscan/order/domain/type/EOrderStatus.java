package com.tookscan.tookscan.order.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EOrderStatus {
    APPLY_COMPLETED("신청완료",1),
    COMPANY_ARRIVED("업체도착",2),
    PAYMENT_WAITING("결제대기",3),
    PAYMENT_COMPLETED("결제완료",4),
    SCAN_WAITING("스캔대기",5),
    SCAN_IN_PROGRESS("스캔중",6),
    RECOVERY_IN_PROGRESS("복원작업",7),
    POST_WAITING("발송대기",8),
    ALL_COMPLETED("작업완료",9),
    CANCEL("취소접수",0);

    private final String description;
    private final Integer code;

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
        };
    }

    public List<EOrderStatus> getDisplayList() {
        return switch (this) {
            case APPLY_COMPLETED -> List.of(APPLY_COMPLETED, COMPANY_ARRIVED);
            case PAYMENT_WAITING -> List.of(PAYMENT_WAITING);
            case PAYMENT_COMPLETED -> List.of(PAYMENT_COMPLETED, SCAN_WAITING, SCAN_IN_PROGRESS);
            case ALL_COMPLETED -> List.of(RECOVERY_IN_PROGRESS, POST_WAITING,
                    ALL_COMPLETED);
            case CANCEL -> List.of(CANCEL);
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        };
    }

    public String toDisplayScanStatusString() {
        return switch (this) {
            case APPLY_COMPLETED, COMPANY_ARRIVED, PAYMENT_WAITING, PAYMENT_COMPLETED, SCAN_WAITING -> "스캔대기";
            case SCAN_IN_PROGRESS -> "스캔중";
            case RECOVERY_IN_PROGRESS, POST_WAITING, ALL_COMPLETED -> "스캔완료";
            case CANCEL -> "취소";
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

