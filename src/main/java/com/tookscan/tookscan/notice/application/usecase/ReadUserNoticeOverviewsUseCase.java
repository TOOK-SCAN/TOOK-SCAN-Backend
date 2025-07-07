package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadUserNoticeOverviewsResponseDto;
import org.springframework.data.domain.Pageable;

@UseCase
public interface ReadUserNoticeOverviewsUseCase {

    /**
     * 사용자 공지사항 목록 조회
     * 공개된 공지사항만 조회하며, 생성일 기준 최신순으로 정렬
     */
    ReadUserNoticeOverviewsResponseDto execute(Pageable pageable);
} 