package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.InitialDocument;
import com.tookscan.tookscan.order.domain.InitialOrder;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.payment.domain.Payment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Integer DELIVERY_EXPIRATION_PERIOD = 14;
    private static final Integer PAYMENT_EXPIRATION_PERIOD = 14;

    public void updateOrderStatus(Order order, EOrderStatus newOrderStatus) {
        order.updateOrderStatus(newOrderStatus);
    }

    public void arriveCompany(Order order) {
        order.updateOrderStatus(EOrderStatus.COMPANY_ARRIVED);
        order.updateArrivedAt(LocalDateTime.now());

        // InitialOrder와 InitialDocument 생성
        createInitialOrderAndDocuments(order);
    }

    public void startScan(Order order) {
        order.updateOrderStatus(EOrderStatus.SCAN_IN_PROGRESS);
        order.updateScanStartedAt(LocalDateTime.now());
    }

    public void completeScan(Order order) {
        order.updateOrderStatus(EOrderStatus.SCAN_COMPLETED);
        order.updateScanCompletedAt(LocalDateTime.now());
    }

    public void startRecovery(Order order) {
        order.updateOrderStatus(EOrderStatus.RECOVERY_IN_PROGRESS);
        order.updateRecoveryStartedAt(LocalDateTime.now());
    }

    private void createInitialOrderAndDocuments(Order order) {
        // InitialOrder 생성
        InitialOrder initialOrder = InitialOrder.builder()
                .isOneDayScan(order.getIsOneDayScan())
                .additionalPriceForOneDayScan(order.getAdditionalPriceForOneDayScan())
                .totalAmount(order.getTotalAmount())
                .usedCoupon(order.getUsedCoupon())
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .build();

        // InitialDocument 생성
        List<InitialDocument> initialDocuments = order.getDocuments().stream()
                .map(document -> InitialDocument.builder()
                        .name(document.getName())
                        .pageCount(document.getPageCount())
                        .recoveryOption(document.getRecoveryOption())
                        .isOcrEnabled(document.getIsOcrEnabled())
                        .cuttingPrice(document.getCuttingPrice())
                        .defaultPricePerPage(document.getDefaultPricePerPage())
                        .additionalPriceForOcr(document.getAdditionalPriceForOcr())
                        .recoveryOptionPrice(document.getRecoveryOptionPrice())
                        .totalAmount(document.getTotalAmount())
                        .initialOrder(initialOrder)
                        .build())
                .toList();

        initialOrder.getInitialDocuments().addAll(initialDocuments);

        order.updateInitialOrder(initialOrder);
    }

    public void finishPayment(Order order, Payment payment) {
        order.updateOrderStatus(EOrderStatus.PAYMENT_COMPLETED);
        order.updatePayment(payment);
    }

    public void updateIsOneDayScan(Order order, Boolean isOneDayScan) {
        order.updateIsOneDayScan(isOneDayScan);
        order.calculateTotalAmount();
    }

    public void cancelOrder(Order order, String reason) {
        order.updateOrderStatus(EOrderStatus.CANCEL);
        order.updateCancelledAt(LocalDateTime.now());
        order.updateCancelReason(reason);
    }

    public void allComplete(Order order) {
        order.updateOrderStatus(EOrderStatus.ALL_COMPLETED);
        order.updateAllCompletedAt(LocalDateTime.now());
    }

    public void completeRecovery(Order order) {
        if (order.getOrderStatus() != EOrderStatus.RECOVERY_IN_PROGRESS) {
            throw new CommonException(ErrorCode.NOT_RECOVERY_IN_PROGRESS);
        }
        order.updateOrderStatus(EOrderStatus.POST_WAITING);
        order.updateRecoveryCompletedAt(LocalDateTime.now());
    }

    public void calculateTotalAmount(Order order) {
        order.calculateTotalAmount();
    }

    public void validateOrderUser(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new CommonException(ErrorCode.NOT_MATCH_ORDER_USER);
        }
    }

    public void validateOrderStatus(Order order, EOrderStatus status, ErrorCode errorCode) {
        if (!order.getOrderStatus().equals(status)) {
            throw new CommonException(errorCode);
        }
    }

    public void validateOrderStatuses(Order order, List<EOrderStatus> statuses, ErrorCode errorCode, String message) {
        if (!statuses.contains(order.getOrderStatus())) {
            throw new CommonException(errorCode, message);
        }
    }

    public void validateOrderStatuses(Order order, List<EOrderStatus> statuses, ErrorCode errorCode) {
        if (!statuses.contains(order.getOrderStatus())) {
            String message = String.format("주문 상태('%s')가 유효하지 않습니다. 허용되는 상태: %s",
                    order.getOrderStatus(), statuses);
            throw new CommonException(errorCode, message);
        }
    }

    public void validateUpdatableOrder(Order order) {
        if (!order.getOrderStatus().equals(EOrderStatus.APPLY_COMPLETED)) {
            throw new CommonException(ErrorCode.NOT_UPDATABLE_ORDER);
        }
    }

    public void validateUpdatableOrderInfo(Order order) {
        if (order.getOrderStatus().getCode() > EOrderStatus.SCAN_COMPLETED.getCode()) {
            throw new CommonException(ErrorCode.NOT_UPDATABLE_ORDER);
        }
    }

    public void updatePaymentExpirationDate(Order order) {
        order.updatePaymentExpirationDate(
                LocalDateTime.now().plusDays(PAYMENT_EXPIRATION_PERIOD + 1).withHour(0).withMinute(0).withSecond(0)
                        .withNano(0));
    }
}
