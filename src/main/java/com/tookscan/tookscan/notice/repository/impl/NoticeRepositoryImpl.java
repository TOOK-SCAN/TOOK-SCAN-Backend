package com.tookscan.tookscan.notice.repository.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.notice.domain.Notice;
import com.tookscan.tookscan.notice.domain.QNotice;
import com.tookscan.tookscan.notice.domain.type.ENoticeSearchType;
import com.tookscan.tookscan.notice.domain.type.ENoticeSortType;
import com.tookscan.tookscan.notice.repository.NoticeRepository;
import com.tookscan.tookscan.notice.repository.mysql.NoticeJpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void save(Notice notice) {
        noticeJpaRepository.save(notice);
    }

    @Override
    public Optional<Notice> findById(Long id) {
        return noticeJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Notice findByIdOrElseThrow(Long id) {
        return noticeJpaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_NOTICE, "ID: " + id));
    }

    @Override
    public Page<Long> findNotices(String search, ENoticeSearchType searchType, String startDate, String endDate,
                                  Boolean isPublic, ENoticeSortType sort, Direction direction, Pageable pageable) {
        QNotice notice = QNotice.notice;

        // 검색 조건 동적 생성
        BooleanExpression predicate = buildPredicate(notice, search, searchType, startDate, endDate, isPublic);

        // 데이터 조회
        List<Long> noticeIds = jpaQueryFactory.select(notice.id)
                .from(notice)
                .where(predicate)
                .orderBy(resolveSort(notice, sort, direction))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회
        long totalCount = Optional.ofNullable(
                jpaQueryFactory.select(notice.count())
                        .from(notice)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        // Page 객체 생성
        return new PageImpl<>(noticeIds, pageable, totalCount);
    }

    @Override
    public List<Notice> findAllByIdIn(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return noticeJpaRepository.findAllByIdInAndDeletedAtIsNull(ids);
    }

    @Override
    public boolean existsById(Long id) {
        return noticeJpaRepository.existsByIdAndDeletedAtIsNull(id);
    }

    @Override
    public void deleteByIdOrElseThrow(Long id) {
        Notice notice = findByIdOrElseThrow(id);
        noticeJpaRepository.delete(notice);
    }

    @Override
    public List<Notice> findPublicNotices() {
        return noticeJpaRepository.findByIsPublicAndDeletedAtIsNull(true);
    }

    @Override
    public Page<Notice> findPublicNoticesWithPagination(Pageable pageable) {
        return noticeJpaRepository.findByIsPublicAndDeletedAtIsNullOrderByCreatedAtDesc(true, pageable);
    }

    @Override
    public Optional<Notice> findPublicNoticeById(Long id) {
        return noticeJpaRepository.findByIdAndIsPublicAndDeletedAtIsNull(id, true);
    }

    @Override
    public long count() {
        return noticeJpaRepository.count();
    }

    private BooleanExpression buildPredicate(QNotice notice, String search, ENoticeSearchType searchType, 
                                           String startDate, String endDate, Boolean isPublic) {
        BooleanExpression predicate = notice.deletedAt.isNull();

        // 검색 조건
        if (StringUtils.hasText(search) && searchType != null) {
            switch (searchType) {
                case TITLE:
                    predicate = predicate.and(notice.title.containsIgnoreCase(search));
                    break;
                case CONTENT:
                    predicate = predicate.and(notice.content.containsIgnoreCase(search));
                    break;
                case TITLE_CONTENT:
                    predicate = predicate.and(notice.title.containsIgnoreCase(search)
                            .or(notice.content.containsIgnoreCase(search)));
                    break;
            }
        }

        // 기간 검색
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            predicate = predicate.and(notice.createdAt.between(startDateTime, endDateTime));
        }

        // 공개 여부 필터
        if (isPublic != null) {
            predicate = predicate.and(notice.isPublic.eq(isPublic));
        }

        return predicate;
    }

    private OrderSpecifier<?> resolveSort(QNotice notice, ENoticeSortType sort, Direction direction) {
        Order order = direction == Direction.ASC ? Order.ASC : Order.DESC;
        
        switch (sort) {
            case VIEW_COUNT:
                return new OrderSpecifier<>(order, notice.viewCount);
            case CREATED_AT:
                return new OrderSpecifier<>(order, notice.createdAt);
            default:
                throw new CommonException(ErrorCode.INVALID_REQUEST, "Invalid sort type: " + sort);
        }
    }
} 