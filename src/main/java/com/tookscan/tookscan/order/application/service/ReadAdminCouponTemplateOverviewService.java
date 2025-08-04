package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminCouponTemplateOverviewUseCase;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminCouponTemplateOverviewResponseDto;
import com.tookscan.tookscan.order.repository.CouponTemplateRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminCouponTemplateOverviewService implements ReadAdminCouponTemplateOverviewUseCase {

    private final CouponTemplateRepository couponTemplateRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminCouponTemplateOverviewResponseDto execute(
            ECouponFormat format,
            ECouponType type,
            String status,
            Integer page,
            Integer size,
            String sort,
            Direction direction
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> couponTemplateIdPages =
                couponTemplateRepository.findCouponTemplatesByFormatAndType(format, type, status, sort, direction, pageable);

        // 리스트로 변환
        List<Long> couponTemplateIds = couponTemplateIdPages.stream().toList();

        // 쿠폰 템플릿 ID로 쿠폰 템플릿과 발급된 쿠폰을 조회
        List<CouponTemplate> couponTemplates = couponTemplateRepository.findAllWithIssuedCouponsByIdIn(couponTemplateIds);

        Map<Long, CouponTemplate> couponTemplateMap = couponTemplates.stream()
                .collect(Collectors.toMap(CouponTemplate::getId, Function.identity()));

        List<CouponTemplate> sortedCouponTemplates = couponTemplateIdPages.stream()
                .map(couponTemplateMap::get)
                .toList();

        return ReadAdminCouponTemplateOverviewResponseDto.of(
                sortedCouponTemplates,
                couponTemplateIdPages
        );
    }
}
