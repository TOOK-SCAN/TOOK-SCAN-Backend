package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderSummaryResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderSummaryUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserOrderSummaryService implements ReadUserOrderSummaryUseCase {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public ReadUserOrderSummaryResponseDto execute(UUID accountId, String orderNumber) {
        Order order = orderRepository.findByOrderNumberOrElseThrow(orderNumber);
        User user = userRepository.findByIdOrElseThrow(accountId);

        orderService.validateOrderUser(order, user);

        return ReadUserOrderSummaryResponseDto.fromEntity(order);
    }
}
