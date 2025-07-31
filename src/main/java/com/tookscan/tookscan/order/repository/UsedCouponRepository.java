package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.UsedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsedCouponRepository {

    UsedCoupon findByIdOrElseThrow(Long id);

    Page<UsedCoupon> findByCouponTemplateIdAndSearch(
            Long couponTemplateId,
            String search,
            String searchType,
            Pageable pageable
    );

    UsedCoupon findWithIssuedCouponByOrderIdOrElseThrow(Long orderId);

    int countByUserIdAndCouponTemplateId(UUID userId, Long couponTemplateId);

    boolean existsByCouponTemplateId(Long couponTemplateId);

    void save(UsedCoupon usedCoupon);
}
