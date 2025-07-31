package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
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
    @BusinessLog(
        domain = "Notice",
        action = "delete notice",
        userType = "Admin"
    )
    public void execute(Long noticeId) {
        noticeRepository.deleteByIdOrElseThrow(noticeId);
        LogContext.put("notice_id", noticeId);
    }
}
