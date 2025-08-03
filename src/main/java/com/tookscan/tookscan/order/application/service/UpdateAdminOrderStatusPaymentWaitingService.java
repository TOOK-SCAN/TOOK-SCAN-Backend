package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.message.domain.event.RequestPaymentMessageEvent;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderStatusPaymentWaitingUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderStatusPaymentWaitingService implements UpdateAdminOrderStatusPaymentWaitingUseCase {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "update order status to payment waiting",
        userType = "Admin"
    )
    public void execute(Long orderId) {

        Order order = orderRepository.findWithUserById(orderId);

        order.updateOrderStatus(EOrderStatus.PAYMENT_WAITING);
        orderService.updatePaymentExpirationDate(order);

        orderRepository.save(order);

        applicationEventPublisher.publishEvent(
                RequestPaymentMessageEvent.of(
                        order.getDocumentsDescription(),
                        order.getTotalAmount(),
                        order.getId(),
                        order.getOrderNumber(),
                        order.getUser().getPhoneNumber(),
                        order.getUser().getEmail(),
                        order.getUser().getName()
                )
        );
        
        LogContext.put("order_id", orderId);
    }
}
