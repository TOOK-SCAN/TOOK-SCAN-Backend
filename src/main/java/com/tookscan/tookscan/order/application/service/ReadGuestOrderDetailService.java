package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.response.ReadGuestOrderDetailResponseDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderDetailService implements ReadGuestOrderDetailUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadGuestOrderDetailResponseDto execute(String orderNumber) {

        // 주문 조회
        Order order = orderRepository.findWithUserAndDeliveryByOrderNumberOrElseThrow(orderNumber);

        return ReadGuestOrderDetailResponseDto.of(order, order.getUser().getId());
    }
}
