package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminCouponTemplateOverviewUseCase;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminCouponTemplateOverviewResponseDto;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadAdminCouponTemplateOverviewService implements ReadAdminCouponTemplateOverviewUseCase {

    private final CouponTemplateRepository couponTemplateRepository;

    @Override
    public ReadAdminCouponTemplateOverviewResponseDto execute(
            ECouponFormat format,
            ECouponType type,
            String status,
            Integer page,
            Integer size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> couponTemplateIdPages =
                couponTemplateRepository.findCouponTemplatesByFormatAndType(format, type, status, pageable);

        List<CouponTemplate> couponTemplates =
                couponTemplateRepository.findAllWithIssuedCouponsByIdIn(couponTemplateIdPages.getContent());

        return ReadAdminCouponTemplateOverviewResponseDto.of(
                couponTemplates,
                couponTemplateIdPages
        );
    }
}
