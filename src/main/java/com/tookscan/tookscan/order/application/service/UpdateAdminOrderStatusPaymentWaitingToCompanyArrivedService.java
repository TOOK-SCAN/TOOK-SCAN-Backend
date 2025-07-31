package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.message.domain.event.CancelPaymentMessageEvent;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderStatusPaymentWaitingToCompanyArrivedUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderStatusPaymentWaitingToCompanyArrivedService implements UpdateAdminOrderStatusPaymentWaitingToCompanyArrivedUseCase {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    @BusinessLog(
            domain = "Order",
            action = "update order status from payment waiting to company arrived",
            userType = "Admin"
    )
    public void execute(Long orderId) {
        // 주문 조회
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        // 주문 상태가 결제 대기 중인지 확인
        if (!order.getOrderStatus().equals(EOrderStatus.PAYMENT_WAITING)) {
            throw new CommonException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 주문 상태를 회사 도착으로 업데이트
        order.updateOrderStatus(EOrderStatus.COMPANY_ARRIVED);

        // 주문 저장
        orderRepository.save(order);

        // 알림톡 발송
        applicationEventPublisher.publishEvent(
                CancelPaymentMessageEvent.of(
                        order.getDocumentsDescription(),
                        order.getDelivery().getPhoneNumber()
                )
        );
        LogContext.put("order_id", orderId);
    }
}
