package com.tookscan.tookscan.notice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.notice.domain.Notice;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadUserNoticeDetailResponseDto extends SelfValidating<ReadUserNoticeDetailResponseDto> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    private String createdAt;

    @Builder
    public ReadUserNoticeDetailResponseDto(String id, String title, String content, String createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.validateSelf();
    }

    public static ReadUserNoticeDetailResponseDto fromEntity(Notice notice) {
        return ReadUserNoticeDetailResponseDto.builder()
                .id(notice.getId().toString())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(notice.getCreatedAt()))
                .build();
    }
} 