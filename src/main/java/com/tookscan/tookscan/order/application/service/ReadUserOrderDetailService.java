package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderDetailResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserOrderDetailService implements ReadUserOrderDetailUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderDetailResponseDto execute(UUID accountId, Long orderId) {
        // 사용자 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        // 주문 조회
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        // 주문자 확인
        orderService.validateOrderUser(order, user);

        return ReadUserOrderDetailResponseDto.of(order, accountId);
    }
}
