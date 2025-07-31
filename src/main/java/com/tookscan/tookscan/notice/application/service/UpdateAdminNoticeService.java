package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.notice.application.usecase.UpdateAdminNoticeUseCase;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.domain.service.NoticeService;
import com.tookscan.tookscan.notice.presentation.dto.request.UpdateAdminNoticeRequestDto;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeDetailResponseDto;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminNoticeService implements UpdateAdminNoticeUseCase {

    private final NoticeRepository noticeRepository;

    private final NoticeService noticeService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Notice",
        action = "update notice",
        userType = "Admin"
    )
    public ReadAdminNoticeDetailResponseDto execute(Long noticeId, UpdateAdminNoticeRequestDto requestDto) {
        Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
        noticeService.updateNotice(notice, requestDto.getTitle(), requestDto.getContent(), requestDto.getIsPublic());
        noticeRepository.save(notice);

        LogContext.put("notice_id", notice.getId());

        return ReadAdminNoticeDetailResponseDto.fromEntity(notice);
    }
}
