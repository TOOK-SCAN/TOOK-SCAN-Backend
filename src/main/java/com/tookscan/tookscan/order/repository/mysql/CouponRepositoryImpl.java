package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon findByCodeOrElseThrow(String code) {
        return couponJpaRepository.findByCode(code)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUPON, "쿠폰 코드: " + code));
    }

    @Override
    public Coupon findByIdOrElseThrow(Long id) {
        return couponJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUPON, "쿠폰 ID: " + id));
    }

    @Override
    public boolean existsByCode(String code) {
        return couponJpaRepository.existsByCode(code);
    }

    @Override
    public void save(Coupon coupon) {
        couponJpaRepository.save(coupon);
    }
}
