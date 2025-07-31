package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderCancelUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
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
    @BusinessLog(
        domain = "Order",
        action = "cancel order",
        userType = "User"
    )
    public void execute(UUID accountId, Long orderId) {
        User user = userRepository.findByIdOrElseThrow(accountId);
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.validateOrderUser(order, user);
        orderService.validateUpdatableOrder(order);
        orderService.cancelOrder(order, "사용자 요청에 의한 주문 취소");

        orderRepository.save(order);
        
        LogContext.put("order_id", orderId);
        LogContext.put("user_id", user.getId());
    }
}
