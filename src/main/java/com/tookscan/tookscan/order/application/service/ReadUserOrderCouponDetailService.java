package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderCouponDetailResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderCouponDetailUseCase;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.service.CouponService;
import com.tookscan.tookscan.order.repository.CouponRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadUserOrderCouponDetailService implements ReadUserOrderCouponDetailUseCase {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderCouponDetailResponseDto execute(UUID accountId, String couponCode) {
        Coupon coupon = couponRepository.findByCodeOrElseThrow(couponCode);
        couponService.validateCouponExpiration(coupon);
        return ReadUserOrderCouponDetailResponseDto.fromEntity(coupon);
    }
}
