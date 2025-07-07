package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadUserNoticeDetailResponseDto;

@UseCase
public interface ReadUserNoticeDetailUseCase {

    /**
     * 사용자 공지사항 상세 조회
     * 공개된 공지사항만 조회 가능하며, 비공개 공지사항일 경우 예외 발생
     */
    ReadUserNoticeDetailResponseDto execute(Long noticeId);
} 