package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminUsedCouponOverviewUseCase;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminUsedCouponOverviewResponseDto;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadAdminUsedCouponOverviewService implements ReadAdminUsedCouponOverviewUseCase {

    private final UsedCouponRepository usedCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    @Override
    public ReadAdminUsedCouponOverviewResponseDto execute(Long id, String search, String searchType, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page-1, size);

        Page<UsedCoupon> usedCouponPage = usedCouponRepository.findByCouponTemplateIdAndSearch(id, search, searchType, pageable);

        CouponTemplate couponTemplate = couponTemplateRepository.findByIdOrElseThrow(id);

        // 응답 DTO 생성
        return ReadAdminUsedCouponOverviewResponseDto.of(usedCouponPage, couponTemplate);
    }
}
