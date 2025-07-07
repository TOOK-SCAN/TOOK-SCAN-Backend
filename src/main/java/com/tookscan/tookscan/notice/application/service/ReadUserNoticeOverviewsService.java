package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.notice.application.usecase.ReadUserNoticeOverviewsUseCase;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadUserNoticeOverviewsResponseDto;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadUserNoticeOverviewsService implements ReadUserNoticeOverviewsUseCase {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserNoticeOverviewsResponseDto execute(Pageable pageable) {
        // 공개된 공지사항만 조회 (생성일 기준 최신순)
        Page<Notice> noticePage = noticeRepository.findPublicNoticesWithPagination(pageable);

        // 응답 DTO 생성
        PageInfoDto pageInfo = PageInfoDto.fromEntity(noticePage);

        return ReadUserNoticeOverviewsResponseDto.of(
                noticePage.getContent(), pageInfo);
    }
} 