package com.tookscan.tookscan.order.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.order.domain.QUsedCoupon;
import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import com.tookscan.tookscan.order.repository.mysql.UsedCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UsedCouponRepositoryImpl implements UsedCouponRepository {

    private final UsedCouponJpaRepository usedCouponJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public UsedCoupon findByIdOrElseThrow(Long id) {
        return usedCouponJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UsedCoupon not found with ID: " + id));
    }

    @Override
    public Page<UsedCoupon> findByCouponTemplateIdAndSearch(
            Long couponTemplateId,
            String search,
            String searchType,
            Pageable pageable
    ) {
        QUsedCoupon usedCoupon = QUsedCoupon.usedCoupon;

        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchType != null) {
            predicate = switch (searchType) {
                case "USER_NAME" -> predicate.and(usedCoupon.user.name.containsIgnoreCase(search));
                case "ORDER_NUMBER" -> predicate.and(usedCoupon.order.orderNumber.containsIgnoreCase(search));
                case "COUPON_CODE" -> predicate.and(usedCoupon.issuedCoupon.code.containsIgnoreCase(search));
                default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
            };
        }

        if (searchType == null && search != null) {
            predicate = predicate.and(usedCoupon.user.name.containsIgnoreCase(search));
        }

        List<UsedCoupon> usedCoupons = jpaQueryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.issuedCoupon.couponTemplate.id.eq(couponTemplateId).and(predicate))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수 조회
        long totalCount = Optional.ofNullable(
                jpaQueryFactory.select(usedCoupon.count())
                        .from(usedCoupon)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(usedCoupons, pageable, totalCount);
    }

    @Override
    public int countByUserIdAndCouponTemplateId(UUID userId, Long couponTemplateId) {
        return usedCouponJpaRepository.countByUserIdAndCouponTemplateId(userId, couponTemplateId);
    }

    @Override
    public void save(UsedCoupon usedCoupon) {
        usedCouponJpaRepository.save(usedCoupon);
    }
}
