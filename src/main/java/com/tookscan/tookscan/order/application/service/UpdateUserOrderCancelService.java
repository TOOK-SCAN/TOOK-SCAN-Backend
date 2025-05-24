package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderCancelUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserOrderCancelService implements UpdateUserOrderCancelUseCase {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    @Override
    @Transactional
    public void execute(UUID accountId, Long orderId) {
        User user = userRepository.findByIdOrElseThrow(accountId);
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.validateOrderUser(order, user);
        orderService.validateUpdatableOrder(order);

        order.updateOrderStatus(EOrderStatus.CANCEL);
        orderRepository.save(order);
    }
}
