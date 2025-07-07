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
public class ReadUserNoticeOverviewsResponseDto extends SelfValidating<ReadUserNoticeOverviewsResponseDto> {

    @JsonProperty("notices")
    private List<NoticeOverviewDto> notices;

    @JsonProperty("page_info")
    private PageInfoDto pageInfo;

    @Builder
    public ReadUserNoticeOverviewsResponseDto(List<NoticeOverviewDto> notices, PageInfoDto pageInfo) {
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

        @JsonProperty("created_at")
        private String createdAt;

        @Builder
        public NoticeOverviewDto(String id, String title, String createdAt) {
            this.id = id;
            this.title = title;
            this.createdAt = createdAt;
            this.validateSelf();
        }

        public static NoticeOverviewDto fromEntity(Notice notice) {
            return NoticeOverviewDto.builder()
                    .id(notice.getId().toString())
                    .title(notice.getTitle())
                    .createdAt(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(notice.getCreatedAt()))
                    .build();
        }
    }

    public static ReadUserNoticeOverviewsResponseDto of(List<Notice> notices, PageInfoDto pageInfo) {
        List<NoticeOverviewDto> noticeItems = notices.stream()
                .map(NoticeOverviewDto::fromEntity)
                .toList();
        return ReadUserNoticeOverviewsResponseDto.builder()
                .notices(noticeItems)
                .pageInfo(pageInfo)
                .build();
    }
} 