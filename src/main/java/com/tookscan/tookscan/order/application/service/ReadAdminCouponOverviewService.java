package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminCouponOverviewUseCase;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminCouponOverviewResponseDto;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadAdminCouponOverviewService implements ReadAdminCouponOverviewUseCase {

    private final CouponTemplateRepository couponTemplateRepository;

    @Override
    public ReadAdminCouponOverviewResponseDto execute(
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

        return ReadAdminCouponOverviewResponseDto.of(
                couponTemplates,
                couponTemplateIdPages
        );
    }
}
