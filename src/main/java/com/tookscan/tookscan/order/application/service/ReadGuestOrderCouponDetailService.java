package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderCouponDetailResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderCouponDetailUseCase;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.service.CouponService;
import com.tookscan.tookscan.order.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderCouponDetailService implements ReadGuestOrderCouponDetailUseCase {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @Override
    @Transactional(readOnly = true)
    public ReadGuestOrderCouponDetailResponseDto execute(String couponCode) {
        Coupon coupon = couponRepository.findByCodeOrElseThrow(couponCode);
        couponService.validateCouponExpiration(coupon);
        if (coupon.isUserOnly()) {
            throw new CommonException(ErrorCode.USER_ONLY_COUPON);
        }
        return ReadGuestOrderCouponDetailResponseDto.fromEntity(coupon);
    }
}
