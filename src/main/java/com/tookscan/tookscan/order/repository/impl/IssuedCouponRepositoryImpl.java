package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.repository.IssuedCouponRepository;
import com.tookscan.tookscan.order.repository.mysql.IssuedCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public IssuedCoupon findByIdOrElseThrow(Long id) {
        return issuedCouponJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ISSUED_COUPON, "발급된 쿠폰 ID: " + id));
    }

    @Override
    public IssuedCoupon findByCodeOrElseThrow(String couponCode) {
        return issuedCouponJpaRepository.findByCode(couponCode)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ISSUED_COUPON, "쿠폰 코드: " + couponCode));
    }

    @Override
    public void save(IssuedCoupon issuedCoupon) {
        issuedCouponJpaRepository.save(issuedCoupon);
    }

    @Override
    public void saveAll(List<IssuedCoupon> issuedCoupons) {
        issuedCouponJpaRepository.saveAll(issuedCoupons);
    }

    @Override
    public boolean existsByCouponCode(String couponCode) {
        return issuedCouponJpaRepository.existsByCode(couponCode);
    }
}
