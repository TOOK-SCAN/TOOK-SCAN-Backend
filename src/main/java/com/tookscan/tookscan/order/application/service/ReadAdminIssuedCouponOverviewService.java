package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminIssuedCouponOverviewUseCase;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminIssuedCouponOverviewResponseDto;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import com.tookscan.tookscan.order.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminIssuedCouponOverviewService implements ReadAdminIssuedCouponOverviewUseCase {

    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    @Transactional(readOnly = true)
    public ReadAdminIssuedCouponOverviewResponseDto execute(Long id, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page-1, size);

        Page<IssuedCoupon> issuedCouponPages = issuedCouponRepository.findByCouponTemplateId(id, pageable);

        CouponTemplate couponTemplate = couponTemplateRepository.findByIdOrElseThrow(id);

        // 응답 DTO 생성
        return ReadAdminIssuedCouponOverviewResponseDto.of(
                issuedCouponPages,
                couponTemplate
        );

    }
}
