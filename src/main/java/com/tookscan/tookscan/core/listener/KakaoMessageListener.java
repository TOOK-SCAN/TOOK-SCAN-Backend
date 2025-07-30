package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
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
        StructuredLoggerUtil.info(log)
                .message("[Kakao Message] Received request scan message event")
                .details(Map.of(
                        "order_name", event.getOrderName(),
                        "order_number", event.getOrderNumber(),
                        "phone_number", event.getPhoneNumber()
                ))
                .log();

        try {
            kakaoMessageUtil.sendRequestScanMessage(
                    event.getOrderName(),
                    event.getOrderNumber(),
                    event.getUserEmail(),
                    event.getPhoneNumber()
            );
            StructuredLoggerUtil.info(log)
                    .message("[Kakao Message] Successfully sent request scan message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "order_number", event.getOrderNumber(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .log();
        } catch (Exception e) {
            StructuredLoggerUtil.error(log)
                    .message("[Kakao Message] Failed to send request scan message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "order_number", event.getOrderNumber(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .exception(e)
                    .log();
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceScanFinishMessageEvent.class})
    public void handleAnnounceScanFinishMessageEvent(AnnounceScanFinishMessageEvent event) {
        StructuredLoggerUtil.info(log)
                .message("[Kakao Message] Received announce scan finish message event")
                .details(Map.of(
                        "order_name", event.getOrderName(),
                        "phone_number", event.getPhoneNumber()
                ))
                .log();

        try {
            kakaoMessageUtil.sendAnnounceScanFinishMessage(
                    event.getUserEmail(),
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
            StructuredLoggerUtil.info(log)
                    .message("[Kakao Message] Successfully sent announce scan finish message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .log();
        } catch (Exception e) {
            StructuredLoggerUtil.error(log)
                    .message("[Kakao Message] Failed to send announce scan finish message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .exception(e)
                    .log();
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {CreateOrderMessageEvent.class})
    public void handleCreateOrderMessageEvent(CreateOrderMessageEvent event) {
        StructuredLoggerUtil.info(log)
                .message("[Kakao Message] Received create order message event")
                .details(Map.of(
                        "order_name", event.getOrderName(),
                        "phone_number", event.getPhoneNumber()
                ));

        try {
            kakaoMessageUtil.sendCreateOrderMessage(
                    event.getUserName(),
                    event.getUserPhone(),
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
            StructuredLoggerUtil.info(log)
                    .message("[Kakao Message] Successfully sent create order message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .log();
        } catch (Exception e) {
            StructuredLoggerUtil.error(log)
                    .message("[Kakao Message] Failed to send create order message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .exception(e)
                    .log();
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {RequestPaymentMessageEvent.class})
    public void handleRequestPaymentMessageEvent(RequestPaymentMessageEvent event) {
        StructuredLoggerUtil.info(log)
                .message("[Kakao Message] Received request payment message event")
                .details(Map.of(
                        "order_name", event.getOrderName(),
                        "order_number", event.getOrderNumber(),
                        "phone_number", event.getPhoneNumber()
                ));

        try {
            kakaoMessageUtil.sendRequestPaymentMessage(
                    event.getOrderName(),
                    event.getOrderPrice(),
                    event.getOrderNumber(),
                    event.getPhoneNumber()
            );
            StructuredLoggerUtil.info(log)
                    .message("[Kakao Message] Successfully sent request payment message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "order_number", event.getOrderNumber(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .log();
        } catch (Exception e) {
            StructuredLoggerUtil.error(log)
                    .message("[Kakao Message] Failed to send request payment message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "order_number", event.getOrderNumber(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .exception(e)
                    .log();
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceDeliveryMessageEvent.class})
    public void handleAnnounceDeliveryMessageEvent(AnnounceDeliveryMessageEvent event) {
        StructuredLoggerUtil.info(log)
                .message("[Kakao Message] Received announce delivery message event")
                .details(Map.of(
                        "order_name", event.getOrderName(),
                        "order_number", event.getOrderNumber(),
                        "phone_number", event.getPhoneNumber()
                ));

        try {
            kakaoMessageUtil.sendAnnounceDeliveryMessage(
                    event.getOrderName(),
                    event.getTrackingNumber(),
                    event.getOrderNumber(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            StructuredLoggerUtil.error(log)
                    .message("[Kakao Message] Failed to send announce delivery message")
                    .details(Map.of(
                            "order_name", event.getOrderName(),
                            "order_number", event.getOrderNumber(),
                            "phone_number", event.getPhoneNumber()
                    ))
                    .exception(e)
                    .log();
        }
    }
}