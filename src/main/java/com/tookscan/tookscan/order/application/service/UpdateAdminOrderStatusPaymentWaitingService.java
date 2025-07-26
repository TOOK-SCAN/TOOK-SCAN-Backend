package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.message.event.RequestPaymentMessageEvent;
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
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdOrElseThrow(orderId);

        order.updateOrderStatus(EOrderStatus.PAYMENT_WAITING);
        orderService.updatePaymentExpirationDate(order);

        orderRepository.save(order);

        applicationEventPublisher.publishEvent(
                RequestPaymentMessageEvent.of(
                        order.getDocumentsDescription(),
                        order.getTotalAmount(),
                        order.getOrderNumber(),
                        order.getDelivery().getPhoneNumber()
                )
        );
    }
}
