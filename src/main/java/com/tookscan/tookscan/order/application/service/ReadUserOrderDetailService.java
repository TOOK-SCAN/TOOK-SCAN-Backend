package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.application.dto.response.ReadUserOrderDetailResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadUserOrderDetailService implements ReadUserOrderDetailUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderDetailResponseDto execute(UUID accountID, Long orderId) {
        // 사용자 조회
        User user = userRepository.findByIdOrElseThrow(accountID);

        // 주문 조회
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        // 주문자 확인
        orderService.validateOrderUser(order, user);

        return ReadUserOrderDetailResponseDto.fromEntity(order);

    }
}
