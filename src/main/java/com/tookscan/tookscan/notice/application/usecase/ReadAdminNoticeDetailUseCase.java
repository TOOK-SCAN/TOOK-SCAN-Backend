package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeDetailResponseDto;

@UseCase
public interface ReadAdminNoticeDetailUseCase {
    /**
     * (관리자) 공지사항 상세 조회 유스케이스
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 응답 DTO
     */
    ReadAdminNoticeDetailResponseDto execute(Long noticeId);
} 