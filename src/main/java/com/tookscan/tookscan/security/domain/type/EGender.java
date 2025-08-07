package com.tookscan.tookscan.security.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EGender {

    MALE("남성"),
    FEMALE("여성"),
    UNKNOWN("알 수 없음")

    ;

    private final String koName;
}
