package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface DeleteAdminNoticeUseCase {
    /**
     * (관리자) 공지사항 삭제 유스케이스
     * @param noticeId 공지사항 ID
     */
    void execute(Long noticeId);
} 