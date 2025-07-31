package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.UsedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UsedCouponRepository {

    UsedCoupon findByIdOrElseThrow(Long id);

    Page<UsedCoupon> findByCouponTemplateIdAndSearch(
            Long couponTemplateId,
            String search,
            String searchType,
            Pageable pageable
    );

    int countByUserIdAndCouponTemplateId(UUID userId, Long couponTemplateId);

    void save(UsedCoupon usedCoupon);
}
