package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.IssuedCoupon;

import java.util.List;

public interface IssuedCouponRepository {

    IssuedCoupon findByIdOrElseThrow(Long id);

    IssuedCoupon findByCodeOrElseThrow(String couponCode);

    void save(IssuedCoupon issuedCoupon);

    void saveAll(List<IssuedCoupon> issuedCoupons);

    boolean existsByCouponCode(String couponCode);
}
