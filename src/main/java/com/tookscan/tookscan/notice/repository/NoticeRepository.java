package com.tookscan.tookscan.notice.repository;

import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.domain.type.ENoticeSearchType;
import com.tookscan.tookscan.notice.domain.type.ENoticeSortType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public interface NoticeRepository {

    /**
     * 공지사항 저장
     */
    void save(Notice notice);

    /**
     * 공지사항 ID로 조회
     */
    Optional<Notice> findById(Long id);

    /**
     * 공지사항 ID로 조회 (없으면 예외 발생)
     */
    Notice findByIdOrElseThrow(Long id);

    /**
     * 공지사항 목록 조회 (검색, 필터링, 정렬, 페이지네이션 지원)
     */
    Page<Long> findNotices(String search, ENoticeSearchType searchType, String startDate, String endDate,
                           Boolean isPublic, ENoticeSortType sort, Direction direction, Pageable pageable);

    /**
     * ID 목록으로 공지사항 조회
     */
    List<Notice> findAllByIdIn(List<Long> ids);

    /**
     * 공지사항 존재 여부 확인
     */
    boolean existsById(Long id);

    /**
     * 공지사항 삭제
     */
    void deleteByIdOrElseThrow(Long id);

    /**
     * 공개된 공지사항 목록 조회
     */
    List<Notice> findPublicNotices();

    /**
     * 공개된 공지사항 목록 조회 (페이지네이션 지원)
     */
    Page<Notice> findPublicNoticesWithPagination(Pageable pageable);

    /**
     * 공개된 공지사항 ID로 조회
     */
    Optional<Notice> findPublicNoticeById(Long id);

    /**
     * 공지사항 전체 개수 조회
     */
    long count();
} 