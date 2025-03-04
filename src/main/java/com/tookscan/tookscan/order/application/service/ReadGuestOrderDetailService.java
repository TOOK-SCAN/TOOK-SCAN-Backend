package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderDetailResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderDetailService implements ReadGuestOrderDetailUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PricePolicyRepository pricePolicyRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadGuestOrderDetailResponseDto execute(String name, String orderNumber) {

        // 주문 조회
        Order order = orderRepository.findByOrderNumberOrElseThrow(orderNumber);

        // 주문자 확인
        orderService.validateOrderNumber(order, name, orderNumber);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(LocalDate.now(), LocalDate.now());

        return ReadGuestOrderDetailResponseDto.fromEntity(order, pricePolicy.getDefaultPrice());
    }
}
