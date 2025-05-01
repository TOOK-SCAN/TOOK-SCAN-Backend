package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.infrastructure.TsidFactory;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    public Order createOrder(User user, boolean isByUser, Delivery delivery, Coupon coupon, Boolean isOneDayScan) {
        String orderNumber = TsidFactory.getFactory().generate().toString();
        return Order.builder()
                .orderNumber(orderNumber)
                .orderStatus(EOrderStatus.APPLY_COMPLETED)
                .isByUser(isByUser)
                .scanCopyrightComplianceAgreed(LocalDateTime.now())
                .illegalDistributionProhibitionAgreed(LocalDateTime.now())
                .cuttingAgreed(LocalDateTime.now())
                .serviceProvisionPeriodAcknowledged(LocalDateTime.now())
                .user(user)
                .delivery(delivery)
                .coupon(coupon)
                .isOneDayScan(isOneDayScan)
                .build();
    }

    public void updateOrderStatus(Order order, EOrderStatus newOrderStatus) {
        order.updateOrderStatus(newOrderStatus);
    }

    public void validateOrderUser(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new CommonException(ErrorCode.NOT_MATCH_ORDER_USER);
        }
    }

    public void validateOrderNumber(Order order, String name, String orderNumber) {
        if (!order.getDelivery().getReceiverName().equals(name) || !order.getOrderNumber().equals(orderNumber)) {
            throw new CommonException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void validateOrderStatus(Order order, EOrderStatus status, ErrorCode errorCode) {
        if (!order.getOrderStatus().equals(status)) {
            throw new CommonException(errorCode);
        }
    }

    public void updateScanTermsAgreed(Order order) {
        order.updateScanTermsAgreed();
    }
}
