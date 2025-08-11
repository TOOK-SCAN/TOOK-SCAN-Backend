package com.tookscan.tookscan.order.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EPdfUploadStatus {
    COMPLETED("완료"),
    FAILED("실패"),
    PENDING("대기 중"),
    IN_PROGRESS("진행 중"),
    ;

    private final String description;

    public static EPdfUploadStatus fromString(String value) {
        return switch (value.toUpperCase()) {
            case "COMPLETED" -> COMPLETED;
            case "FAILED" -> FAILED;
            case "PENDING" -> PENDING;
            case "IN_PROGRESS" -> IN_PROGRESS;
            default -> throw new IllegalArgumentException("Invalid PDF upload status: " + value);
        };
    }
}

