package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.notice.presentation.dto.request.UpdateAdminNoticeRequestDto;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeDetailResponseDto;

@UseCase
public interface UpdateAdminNoticeUseCase {
    /**
     * (관리자) 공지사항 수정 유스케이스
     * @param noticeId 공지사항 ID
     * @param requestDto 공지사항 수정 요청 DTO
     * @return 수정된 공지사항 상세 응답 DTO
     */
    ReadAdminNoticeDetailResponseDto execute(Long noticeId, UpdateAdminNoticeRequestDto requestDto);
} 