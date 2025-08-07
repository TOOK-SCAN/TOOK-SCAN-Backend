package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.message.domain.event.AnnounceDeliveryMessageEvent;
import com.tookscan.tookscan.message.domain.event.AnnounceScanFinishMessageEvent;
import com.tookscan.tookscan.message.domain.event.CancelPaymentMessageEvent;
import com.tookscan.tookscan.message.domain.event.CreateOrderMessageEvent;
import com.tookscan.tookscan.message.domain.event.RequestPaymentMessageEvent;
import com.tookscan.tookscan.message.domain.event.RequestScanMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMessageListener {

    private final KakaoMessageUtil kakaoMessageUtil;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {RequestScanMessageEvent.class})
    public void handleRequestScanMessageEvent(RequestScanMessageEvent event) {
        log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_id", event.getOrderId())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Received request scan message event");

        try {
            kakaoMessageUtil.sendRequestScanMessage(
                    event.getOrderName(),
                    event.getOrderId(),
                    event.getUserEmail(),
                    event.getPhoneNumber()
            );
            log.atInfo()
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("order_id", event.getOrderId())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Successfully sent request scan message");
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("order_id", event.getOrderId())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Failed to send request scan message");
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceScanFinishMessageEvent.class})
    public void handleAnnounceScanFinishMessageEvent(AnnounceScanFinishMessageEvent event) {
        log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Received announce scan finish message event");

        try {
            kakaoMessageUtil.sendAnnounceScanFinishMessage(
                    event.getUserEmail(),
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
            log.atInfo()
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Successfully sent announce scan finish message");
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Failed to send announce scan finish message");
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {CreateOrderMessageEvent.class})
    public void handleCreateOrderMessageEvent(CreateOrderMessageEvent event) {
        log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Received create order message event");

        try {
            kakaoMessageUtil.sendCreateOrderMessage(
                    event.getUserName(),
                    event.getUserPhone(),
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
            log.atInfo()
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Successfully sent create order message");
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Failed to send create order message");
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {RequestPaymentMessageEvent.class})
    public void handleRequestPaymentMessageEvent(RequestPaymentMessageEvent event) {
        log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_id", event.getOrderId())
                .addKeyValue("order_price", event.getOrderPrice())
                .addKeyValue("customer_key", event.getCustomerKey())
                .log("[Kakao Message] Received request payment message event");

        try {
            kakaoMessageUtil.sendRequestPaymentMessage(
                    event.getOrderName(),
                    event.getOrderPrice(),
                    event.getOrderId(),
                    event.getOrderNumber(),
                    event.getEmail(),
                    event.getUserName(),
                    event.getPhoneNumber(),
                    event.getCustomerKey()
            );
            log.atInfo()
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("order_id", event.getOrderId())
                    .addKeyValue("order_price", event.getOrderPrice())
                    .addKeyValue("customer_key", event.getCustomerKey())
                    .log("[Kakao Message] Successfully sent request payment message");
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("order_id", event.getOrderId())
                    .addKeyValue("order_price", event.getOrderPrice())
                    .addKeyValue("customer_key", event.getCustomerKey())
                    .log("[Kakao Message] Failed to send request payment message");
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceDeliveryMessageEvent.class})
    public void handleAnnounceDeliveryMessageEvent(AnnounceDeliveryMessageEvent event) {
        log.atInfo()
                .addKeyValue("delivery_id", event.getDeliveryId())
                .addKeyValue("tracking_number", event.getTrackingNumber())
                .log("[Kakao Message] Received announce delivery message event");

        try {
            kakaoMessageUtil.sendAnnounceDeliveryMessage(
                    event.getOrderName(),
                    event.getTrackingNumber(),
                    event.getDeliveryId(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("delivery_id", event.getDeliveryId())
                    .addKeyValue("tracking_number", event.getTrackingNumber())
                    .log("[Kakao Message] Failed to send announce delivery message");
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {CancelPaymentMessageEvent.class})
    public void handleCancelPaymentMessageEvent(CancelPaymentMessageEvent event) {
        log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("phone_number", event.getPhoneNumber());

        try {
            kakaoMessageUtil.sendCancelPaymentMessage(
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("order_name", event.getOrderName())
                    .addKeyValue("phone_number", event.getPhoneNumber())
                    .log("[Kakao Message] Failed to send cancel payment message");
        }
    }
}