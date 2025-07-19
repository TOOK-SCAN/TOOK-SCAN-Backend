package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderStatusPaymentWaitingUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderStatusPaymentWaitingService implements UpdateAdminOrderStatusPaymentWaitingUseCase {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final KakaoMessageUtil kakaoMessageUtil;

    @Override
    @Transactional
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdOrElseThrow(orderId);

        order.updateOrderStatus(EOrderStatus.PAYMENT_WAITING);
        orderService.updatePaymentExpirationDate(order);

        // 결제 요청 메세지 전송
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kakaoMessageUtil.sendRequestPaymentMessage(
                        order.getDocumentsDescription(),
                        order.getTotalAmount(),
                        order.getOrderNumber(),
                        order.getDelivery().getPhoneNumber()
                );
            }
        });

        orderRepository.save(order);
    }
}
