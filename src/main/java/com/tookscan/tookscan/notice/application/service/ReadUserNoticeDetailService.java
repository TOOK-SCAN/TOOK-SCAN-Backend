package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.notice.application.usecase.ReadUserNoticeDetailUseCase;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.domain.service.NoticeService;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadUserNoticeDetailResponseDto;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadUserNoticeDetailService implements ReadUserNoticeDetailUseCase {

    private final NoticeRepository noticeRepository;

    private final NoticeService noticeService;

    @Override
    @Transactional(readOnly = true)
    public ReadUserNoticeDetailResponseDto execute(Long noticeId) {

        // 공개된 공지사항만 조회 (비공개 공지사항일 경우 예외 발생)
        Notice notice = noticeRepository.findPublicNoticeById(noticeId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NOTICE, "공개된 공지사항이 없습니다. ID: " + noticeId));

        // 조회수 증가
        noticeService.increaseViewCount(notice);
        noticeRepository.save(notice);

        return ReadUserNoticeDetailResponseDto.fromEntity(notice);
    }
} 