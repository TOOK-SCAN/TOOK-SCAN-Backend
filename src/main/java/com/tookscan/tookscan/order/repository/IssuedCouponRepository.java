package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.IssuedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IssuedCouponRepository {

    IssuedCoupon findByIdOrElseThrow(Long id);

    IssuedCoupon findByCodeOrElseThrow(String couponCode);

    void save(IssuedCoupon issuedCoupon);

    void saveAll(List<IssuedCoupon> issuedCoupons);

    boolean existsByCouponCode(String couponCode);

    Page<IssuedCoupon> findByCouponTemplateIdPage(Long couponTemplateId, Pageable pageable);

    List<IssuedCoupon> findByCouponTemplateId(Long couponTemplateId);

    int countByCouponTemplateId(Long couponTemplateId);
}
