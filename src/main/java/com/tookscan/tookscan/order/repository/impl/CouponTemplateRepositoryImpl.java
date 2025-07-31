package com.tookscan.tookscan.order.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.QCouponTemplate;
import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import com.tookscan.tookscan.order.repository.mysql.CouponTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponTemplateRepositoryImpl implements CouponTemplateRepository {

    private final CouponTemplateJpaRepository couponTemplateJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public CouponTemplate findByIdOrElseThrow(Long id) {
        return couponTemplateJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUPON_TEMPLATE, "쿠폰 템플릿 ID: " + id));
    }

    @Override
    public Page<Long> findCouponTemplatesByFormatAndType(
            ECouponFormat format,
            ECouponType type,
            String status,
            Pageable pageable
    ) {
        QCouponTemplate couponTemplate = QCouponTemplate.couponTemplate;

        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (format != null) {
            predicate = predicate.and(couponTemplate.format.eq(format));
        }

        if (type != null) {
            predicate = predicate.and(couponTemplate.type.eq(type));
        }

        if (status != null) {
            LocalDateTime now = LocalDateTime.now();

            switch (status) {
                case "WAITING" -> predicate = predicate.and(couponTemplate.startDateTime.gt(now));
                case "ACTIVE" -> predicate = predicate.and(
                        couponTemplate.startDateTime.loe(now).and(couponTemplate.endDateTime.goe(now)));
                case "INACTIVE" -> predicate = predicate.and(couponTemplate.endDateTime.lt(now));
                case "ALL" -> { /* 아무것도 안 추가 */ }
                default -> throw new CommonException(ErrorCode.INVALID_ARGUMENT);
            }
        }

        List<Long> couponTemplateIds = jpaQueryFactory
                .select(couponTemplate.id)
                .from(couponTemplate)
                .where(predicate)
                .orderBy(couponTemplate.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회
        long totalCount = Optional.ofNullable(
                jpaQueryFactory.select(couponTemplate.count())
                        .from(couponTemplate)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(couponTemplateIds, pageable, totalCount);
    }

    @Override
    public List<CouponTemplate> findAllWithIssuedCouponsByIdIn(List<Long> ids) {
        return couponTemplateJpaRepository.findAllWithIssuedCouponsByIdIn(ids);
    }


    @Override
    public void save(CouponTemplate couponTemplate) {
        couponTemplateJpaRepository.save(couponTemplate);
    }
}
