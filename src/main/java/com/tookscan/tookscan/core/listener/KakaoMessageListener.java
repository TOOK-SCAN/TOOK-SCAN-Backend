package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.message.domain.event.AnnounceDeliveryMessageEvent;
import com.tookscan.tookscan.message.domain.event.AnnounceScanFinishMessageEvent;
import com.tookscan.tookscan.message.domain.event.CreateOrderMessageEvent;
import com.tookscan.tookscan.message.domain.event.RequestPaymentMessageEvent;
import com.tookscan.tookscan.message.domain.event.RequestScanMessageEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class KakaoMessageListener {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageListener.class);
    private final KakaoMessageUtil kakaoMessageUtil;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {RequestScanMessageEvent.class})
    public void handleRequestScanMessageEvent(RequestScanMessageEvent event) {
        log.atInfo()
            .addKeyValue("order_name", event.getOrderName())
            .addKeyValue("order_number", event.getOrderNumber())
            .addKeyValue("phone_number", event.getPhoneNumber())
            .log("[Kakao Message] Received request scan message event");

        try {
            kakaoMessageUtil.sendRequestScanMessage(
                    event.getOrderName(),
                    event.getOrderNumber(),
                    event.getUserEmail(),
                    event.getPhoneNumber()
            );
            log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_number", event.getOrderNumber())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Successfully sent request scan message");
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_number", event.getOrderNumber())
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
            .addKeyValue("order_number", event.getOrderNumber())
            .addKeyValue("phone_number", event.getPhoneNumber())
            .log("[Kakao Message] Received request payment message event");

        try {
            kakaoMessageUtil.sendRequestPaymentMessage(
                    event.getOrderName(),
                    event.getOrderPrice(),
                    event.getOrderNumber(),
                    event.getPhoneNumber()
            );
            log.atInfo()
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_number", event.getOrderNumber())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Successfully sent request payment message");
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_number", event.getOrderNumber())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Failed to send request payment message");
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceDeliveryMessageEvent.class})
    public void handleAnnounceDeliveryMessageEvent(AnnounceDeliveryMessageEvent event) {
        log.atInfo()
            .addKeyValue("order_name", event.getOrderName())
            .addKeyValue("order_number", event.getOrderNumber())
            .addKeyValue("phone_number", event.getPhoneNumber())
            .log("[Kakao Message] Received announce delivery message event");

        try {
            kakaoMessageUtil.sendAnnounceDeliveryMessage(
                    event.getOrderName(),
                    event.getTrackingNumber(),
                    event.getOrderNumber(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("order_name", event.getOrderName())
                .addKeyValue("order_number", event.getOrderNumber())
                .addKeyValue("phone_number", event.getPhoneNumber())
                .log("[Kakao Message] Failed to send announce delivery message");
        }
    }
}