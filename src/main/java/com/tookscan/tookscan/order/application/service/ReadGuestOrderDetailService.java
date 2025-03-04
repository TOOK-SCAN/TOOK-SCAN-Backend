package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderDetailResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderDetailService implements ReadGuestOrderDetailUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public ReadGuestOrderDetailResponseDto execute(String name, String orderNumber) {

        // 주문 조회
        Order order = orderRepository.findByOrderNumberOrElseThrow(orderNumber);

        // 주문자 확인
        orderService.validateOrderNumber(order, name, orderNumber);

        return ReadGuestOrderDetailResponseDto.fromEntity(order);
    }
}
