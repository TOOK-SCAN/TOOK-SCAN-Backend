package com.tookscan.tookscan.notice.domain.type;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ENoticeSearchType {
    
    TITLE("title", "제목"),
    CONTENT("content", "내용"),
    TITLE_CONTENT("title-content", "제목+내용");

    private final String value;
    private final String description;

    public static ENoticeSearchType fromString(String value) {

        return switch (value.toUpperCase()) {
            case "TITLE" -> TITLE;
            case "CONTENT" -> CONTENT;
            case "TITLE-CONTENT" -> TITLE_CONTENT;
            default -> throw new CommonException(ErrorCode.INVALID_ENUM_TYPE, "Invalid search type: " + value);
        };
    }
} 