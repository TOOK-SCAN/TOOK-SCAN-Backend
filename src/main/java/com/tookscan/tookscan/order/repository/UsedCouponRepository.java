package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.UsedCoupon;

import java.util.List;
import java.util.UUID;

public interface UsedCouponRepository {

    UsedCoupon findByIdOrElseThrow(Long id);

    int countByUserIdAndCouponTemplateId(UUID userId, Long couponTemplateId);

    void save(UsedCoupon usedCoupon);
}
