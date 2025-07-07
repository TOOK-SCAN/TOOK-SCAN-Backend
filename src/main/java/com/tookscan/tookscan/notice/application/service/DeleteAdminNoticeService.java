package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.notice.application.usecase.DeleteAdminNoticeUseCase;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAdminNoticeService implements DeleteAdminNoticeUseCase {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public void execute(Long noticeId) {
        noticeRepository.deleteByIdOrElseThrow(noticeId);
    }
} 