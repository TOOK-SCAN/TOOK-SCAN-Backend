package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.Coupon;

public interface CouponRepository {
    Coupon findByCodeOrElseThrow(String code);

    Coupon findByIdOrElseThrow(Long id);

    boolean existsByCode(String code);

    void save(Coupon coupon);
}
