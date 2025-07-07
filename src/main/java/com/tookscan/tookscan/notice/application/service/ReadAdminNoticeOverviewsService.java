package com.tookscan.tookscan.notice.application.service;

import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.notice.application.usecase.ReadAdminNoticeOverviewsUseCase;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.domain.type.ENoticeSearchType;
import com.tookscan.tookscan.notice.domain.type.ENoticeSortType;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeOverviewsResponseDto;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadAdminNoticeOverviewsService implements ReadAdminNoticeOverviewsUseCase {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminNoticeOverviewsResponseDto execute(Integer page, Integer size, String search,
                                                       ENoticeSearchType searchType, ENoticeSortType sort, String direction,
                                                       String startDate, String endDate, Boolean isPublic) {
        // 페이지네이션 설정
        Pageable pageable = PageRequest.of(
                page - 1, // 0-based index
                size
        );

        // 정렬 방향 설정
        Direction sortDirection = Direction.DESC;
        if (direction != null) {
            sortDirection = direction.equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC;
        }

        // 공지사항 ID 목록 조회
        Page<Long> noticeIdPage = noticeRepository.findNotices(
                search,
                searchType,
                startDate,
                endDate,
                isPublic,
                sort,
                sortDirection,
                pageable
        );

        // 실제 공지사항 엔티티 조회
        List<Notice> notices = noticeRepository.findAllByIdIn(noticeIdPage.getContent());

        // 페이지 정보 생성
        PageInfoDto pageInfo = PageInfoDto.fromEntity(noticeIdPage);

        return ReadAdminNoticeOverviewsResponseDto.of(
                notices,
                pageInfo
        );
    }
} 