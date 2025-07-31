package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.usecase.DeleteAdminCouponTemplateUseCase;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAdminCouponTemplateService implements DeleteAdminCouponTemplateUseCase {

    private final CouponTemplateRepository couponTemplateRepository;
    private final UsedCouponRepository usedCouponRepository;

    @Override
    public void execute(Long id) {

        // 사용된 쿠폰이 있는지 확인
        if (usedCouponRepository.existsByCouponTemplateId(id)) {
            throw new CommonException(ErrorCode.ALREADY_USED_COUPON);
        }

        couponTemplateRepository.deleteById(id);
    }
}
