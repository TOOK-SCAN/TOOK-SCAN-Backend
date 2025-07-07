package com.tookscan.tookscan.notice.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.notice.domain.type.ENoticeSearchType;
import com.tookscan.tookscan.notice.domain.type.ENoticeSortType;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeOverviewsResponseDto;

@UseCase
public interface ReadAdminNoticeOverviewsUseCase {
    /**
     * (관리자) 공지사항 목록 조회 유스케이스
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param search 검색어
     * @param searchType 검색 타입
     * @param sort 정렬 기준
     * @param direction 정렬 방향
     * @param startDate 시작일
     * @param endDate 종료일
     * @param isPublic 공개 여부
     * @return 공지사항 목록 응답 DTO
     */
    ReadAdminNoticeOverviewsResponseDto execute(Integer page, Integer size, String search,
                                                ENoticeSearchType searchType, ENoticeSortType sort, String direction,
                                                String startDate, String endDate, Boolean isPublic);
} 