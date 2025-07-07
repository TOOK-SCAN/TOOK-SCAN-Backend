package com.tookscan.tookscan.notice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.notice.domain.Notice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadAdminNoticeDetailResponseDto extends SelfValidating<ReadAdminNoticeDetailResponseDto> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("view_count")
    private Long viewCount;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @Builder
    public ReadAdminNoticeDetailResponseDto(String id, String title, String content, Boolean isPublic,
                                           Long viewCount, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.validateSelf();
    }

    public static ReadAdminNoticeDetailResponseDto fromEntity(Notice notice) {

        LocalDateTime updatedAt = notice.getUpdatedAt();

        return ReadAdminNoticeDetailResponseDto.builder()
                .id(notice.getId().toString())
                .title(notice.getTitle())
                .content(notice.getContent())
                .isPublic(notice.getIsPublic())
                .viewCount(notice.getViewCount())
                .createdAt(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(notice.getCreatedAt()))
                .updatedAt(updatedAt != null
                        ? DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(updatedAt)
                        : null)
                .build();
    }
} 