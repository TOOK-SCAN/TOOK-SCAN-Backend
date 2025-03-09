package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.dto.request.CreateAdminOrderCouponRequestDto;
import com.tookscan.tookscan.order.application.usecase.CreateAdminOrderCouponUseCase;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.service.CouponService;
import com.tookscan.tookscan.order.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminOrderCouponService implements CreateAdminOrderCouponUseCase {
    private final Integer MAX_ATTEMPTS = 10;

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @Override
    @Transactional
    public void execute(CreateAdminOrderCouponRequestDto requestDto) {
        int count = 1;

        if (!requestDto.isPossibleDuplicatedApply()) {
            count = requestDto.count();
        }

        for (int i = 0; i < count; i++) {
            String couponCode = createUniqueCouponCode(requestDto.tag());
            Coupon coupon = couponService.createCoupon(requestDto.name(), couponCode, requestDto.description(),
                    requestDto.type(), requestDto.discountPrice(), requestDto.discountPercent(),
                    requestDto.startDateTime(),
                    requestDto.endDateTime(), requestDto.isPossibleDuplicatedApply());
            couponRepository.save(coupon);
        }
    }

    private String createUniqueCouponCode(String tag) {
        String couponCode;
        int attempts = 0;
        do {
            couponCode = couponService.generateCouponCode(tag);
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "중복되지 않는 쿠폰 코드를 생성하지 못했습니다.");
            }
        } while (couponRepository.existsByCode(couponCode));
        return couponCode;
    }
}
