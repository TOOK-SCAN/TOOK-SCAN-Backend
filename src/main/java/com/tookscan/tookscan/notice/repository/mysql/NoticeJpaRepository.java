package com.tookscan.tookscan.notice.repository.mysql;

import com.tookscan.tookscan.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {

    /**
     * ID로 공지사항 조회 (삭제되지 않은 것만)
     */
    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);

    /**
     * 공개 상태별 공지사항 목록 조회
     */
    List<Notice> findByIsPublicAndDeletedAtIsNull(Boolean isPublic);

    /**
     * 공지사항 존재 여부 확인
     */
    boolean existsByIdAndDeletedAtIsNull(Long id);

    /**
     * ID 목록으로 공지사항 조회 (삭제되지 않은 것만)
     */
    List<Notice> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

    /**
     * 공개된 공지사항 목록 조회 (페이지네이션 지원, 생성일 기준 최신순)
     */
    Page<Notice> findByIsPublicAndDeletedAtIsNullOrderByCreatedAtDesc(Boolean isPublic, Pageable pageable);

    /**
     * 공개된 공지사항 ID로 조회
     */
    Optional<Notice> findByIdAndIsPublicAndDeletedAtIsNull(Long id, Boolean isPublic);
} 