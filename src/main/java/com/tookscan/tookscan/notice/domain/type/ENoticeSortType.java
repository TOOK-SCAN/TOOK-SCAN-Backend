package com.tookscan.tookscan.notice.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENoticeSortType {
    
    CREATED_AT("created-at", "작성일"),
    VIEW_COUNT("view-count", "조회수");

    private final String value;
    private final String description;

    public static ENoticeSortType fromString(String value) {
        return switch (value.toUpperCase()) {
            case "CREATED-AT" -> CREATED_AT;
            case "VIEW-COUNT" -> VIEW_COUNT;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE, "Invalid sort type: " + value);
        };
    }
} 