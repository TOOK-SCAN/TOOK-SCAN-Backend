package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.notice.application.usecase.ReadAdminNoticeDetailUseCase;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeDetailResponseDto;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminNoticeDetailService implements ReadAdminNoticeDetailUseCase {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public ReadAdminNoticeDetailResponseDto execute(Long noticeId) {
        Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
        return ReadAdminNoticeDetailResponseDto.fromEntity(notice);
    }
} 