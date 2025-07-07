package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.notice.presentation.dto.request.CreateAdminNoticeRequestDto;

@UseCase
public interface CreateAdminNoticeUseCase {
    /**
     * (관리자) 공지사항 생성 유스케이스
     * @param requestDto 공지사항 생성 요청 DTO
     */
    void execute(CreateAdminNoticeRequestDto requestDto);
} 