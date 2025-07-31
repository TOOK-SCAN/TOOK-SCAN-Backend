package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import com.tookscan.tookscan.order.repository.mysql.CouponTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponTemplateRepositoryImpl implements CouponTemplateRepository {
    private final CouponTemplateJpaRepository couponTemplateJpaRepository;

    @Override
    public CouponTemplate findByIdOrElseThrow(Long id) {
        return couponTemplateJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_COUPON_TEMPLATE, "쿠폰 템플릿 ID: " + id));
    }

    @Override
    public void save(CouponTemplate couponTemplate) {
        couponTemplateJpaRepository.save(couponTemplate);
    }
}
