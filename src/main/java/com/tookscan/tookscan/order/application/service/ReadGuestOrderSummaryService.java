package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderSummaryResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderSummaryUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderSummaryService implements ReadGuestOrderSummaryUseCase {

    private final OrderRepository orderRepository;
    private final PricePolicyRepository pricePolicyRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadGuestOrderSummaryResponseDto execute(String orderNumber) {
        Order order = orderRepository.findByOrderNumberOrElseThrow(orderNumber);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(LocalDate.now(), LocalDate.now());

        return ReadGuestOrderSummaryResponseDto.fromEntity(order, pricePolicy.getDefaultPrice());
    }
}
