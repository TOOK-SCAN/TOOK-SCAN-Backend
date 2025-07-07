package com.tookscan.tookscan.notice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.notice.domain.Notice;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReadAdminNoticeOverviewsResponseDto extends SelfValidating<ReadAdminNoticeOverviewsResponseDto> {

    @JsonProperty("notices")
    private List<NoticeOverviewDto> notices;

    @JsonProperty("page_info")
    private PageInfoDto pageInfo;

    @Builder
    public ReadAdminNoticeOverviewsResponseDto(List<NoticeOverviewDto> notices, PageInfoDto pageInfo) {
        this.notices = notices;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    @Getter
    @RequiredArgsConstructor
    public static class NoticeOverviewDto extends SelfValidating<NoticeOverviewDto> {

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

        @Builder
        public NoticeOverviewDto(String id, String title, String content, Boolean isPublic,
                                 Long viewCount, String createdAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.isPublic = isPublic;
            this.viewCount = viewCount;
            this.createdAt = createdAt;
            this.validateSelf();
        }

        public static NoticeOverviewDto fromEntity(Notice notice) {
            return NoticeOverviewDto.builder()
                    .id(notice.getId().toString())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .isPublic(notice.getIsPublic())
                    .viewCount(notice.getViewCount())
                    .createdAt(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(notice.getCreatedAt()))
                    .build();
        }
    }

    public static ReadAdminNoticeOverviewsResponseDto of(List<Notice> notices, PageInfoDto pageInfo) {
        List<NoticeOverviewDto> noticeItems = notices.stream()
                .map(NoticeOverviewDto::fromEntity)
                .toList();
        return ReadAdminNoticeOverviewsResponseDto.builder()
                .notices(noticeItems)
                .pageInfo(pageInfo)
                .build();
    }
} 