package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderStatusPaymentWaitingUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderStatusPaymentWaitingService implements UpdateAdminOrderStatusPaymentWaitingUseCase {

    private final OrderRepository orderRepository;

    private final KakaoMessageUtil kakaoMessageUtil;

    @Value("${solapi.sender}")
    private String sender;

    @Override
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdOrElseThrow(orderId);

        order.updateOrderStatus(EOrderStatus.PAYMENT_WAITING);

        kakaoMessageUtil.sendRequestPaymentMessage(
                order.getUser() != null ? ESecurityRole.USER : ESecurityRole.GUEST,
                order.getUser() != null ? order.getUser().getName() : order.getDelivery().getReceiverName(),
                order.getOrderNumber(),
                order.getId(),
                order.getUser() != null ? order.getUser().getPhoneNumber() : order.getDelivery().getPhoneNumber(),
                sender
        );

        orderRepository.save(order);
    }
}
