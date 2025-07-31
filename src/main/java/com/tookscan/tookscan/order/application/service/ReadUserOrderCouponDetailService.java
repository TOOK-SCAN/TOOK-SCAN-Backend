package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderCouponDetailUseCase;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderCouponDetailResponseDto;
import com.tookscan.tookscan.order.repository.IssuedCouponRepository;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserOrderCouponDetailService implements ReadUserOrderCouponDetailUseCase {

    private final IssuedCouponRepository issuedCouponRepository;
    private final UsedCouponRepository usedCouponRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderCouponDetailResponseDto execute(UUID accountId, String couponCode) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByCodeOrElseThrow(couponCode);
        this.validateCouponExpiration(accountId, issuedCoupon);
        return ReadUserOrderCouponDetailResponseDto.fromEntity(issuedCoupon);
    }

    public void validateCouponExpiration(UUID accountId, IssuedCoupon issuedCoupon) {

        // 쿠폰의 사용 기간 확인
        if (issuedCoupon.getCouponTemplate().getStartDateTime() != null && issuedCoupon.getCouponTemplate().getEndDateTime() != null) {
            if (issuedCoupon.getCouponTemplate().getStartDateTime().isAfter(LocalDate.now().atStartOfDay())) {
                throw new CommonException(ErrorCode.NOT_AVAILABLE_COUPON);
            }
            if (issuedCoupon.getCouponTemplate().getEndDateTime().isBefore(LocalDate.now().atStartOfDay())) {
                throw new CommonException(ErrorCode.NOT_AVAILABLE_COUPON);
            }
        }

        // 한 사용자당 쿠폰 사용 횟수를 넘겼는지 확인
        if (issuedCoupon.getCouponTemplate().getMaxUsedPerUserCount() != null && usedCouponRepository.countByUserIdAndCouponTemplateId(accountId, issuedCoupon.getCouponTemplate().getId()) > issuedCoupon.getCouponTemplate().getMaxUsedPerUserCount()) {
            throw new CommonException(ErrorCode.EXCEEDED_MAX_USED_COUPON_PER_USER);

        }

        // 전체 사용자의 쿠폰 사용 횟수를 넘겼는지 확인
        if (issuedCoupon.getMaxUsedCount() != null && issuedCoupon.getUsedCount() >= issuedCoupon.getMaxUsedCount()) {
            throw new CommonException(ErrorCode.EXCEEDED_MAX_USED_COUPON);
        }
    }
}
