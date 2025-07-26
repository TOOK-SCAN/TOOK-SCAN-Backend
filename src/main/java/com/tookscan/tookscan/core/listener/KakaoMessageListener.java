package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.message.event.AnnounceScanFinishMessageEvent;
import com.tookscan.tookscan.message.event.AnnounceDeliveryMessageEvent;
import com.tookscan.tookscan.message.event.CreateOrderMessageEvent;
import com.tookscan.tookscan.message.event.RequestPaymentMessageEvent;
import com.tookscan.tookscan.message.event.RequestScanMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMessageListener {

    private final KakaoMessageUtil kakaoMessageUtil;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {RequestScanMessageEvent.class})
    public void handleRequestScanMessageEvent(RequestScanMessageEvent event) {
        log.info(
                "\n----------------------------------\n[ 스캔 요청 메시지 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                "주문명: " + event.getOrderName(),
                "주문번호: " + event.getOrderNumber()
        );

        try {
            kakaoMessageUtil.sendRequestScanMessage(
                    event.getOrderName(),
                    event.getOrderNumber(),
                    event.getUserEmail(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.error("스캔 요청 메시지 발송 실패", e);
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceScanFinishMessageEvent.class})
    public void handleAnnounceScanFinishMessageEvent(AnnounceScanFinishMessageEvent event) {
        log.info(
                "\n----------------------------------\n[ 스캔 완료 알림 메시지 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                "사용자 이메일: " + event.getUserEmail(),
                "주문명: " + event.getOrderName()
        );

        try {
            kakaoMessageUtil.sendAnnounceScanFinishMessage(
                    event.getUserEmail(),
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.error("스캔 완료 알림 메시지 발송 실패", e);
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {CreateOrderMessageEvent.class})
    public void handleCreateOrderMessageEvent(CreateOrderMessageEvent event) {
        log.info(
                "\n----------------------------------\n[ 주문 생성 메시지 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                "사용자명: " + event.getUserName(),
                "주문명: " + event.getOrderName()
        );

        try {
            kakaoMessageUtil.sendCreateOrderMessage(
                    event.getUserName(),
                    event.getUserPhone(),
                    event.getOrderName(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.error("주문 생성 메시지 발송 실패", e);
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {RequestPaymentMessageEvent.class})
    public void handleRequestPaymentMessageEvent(RequestPaymentMessageEvent event) {
        log.info(
                "\n----------------------------------\n[ 결제 요청 메시지 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                "주문명: " + event.getOrderName(),
                "주문 금액: " + event.getOrderPrice()
        );

        try {
            kakaoMessageUtil.sendRequestPaymentMessage(
                    event.getOrderName(),
                    event.getOrderPrice(),
                    event.getOrderNumber(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.error("결제 요청 메시지 발송 실패", e);
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AnnounceDeliveryMessageEvent.class})
    public void handleAnnounceDeliveryMessageEvent(AnnounceDeliveryMessageEvent event) {
        log.info(
                "\n----------------------------------\n[ 배송 안내 메시지 이벤트 처리 ]\n{}\n{}\n----------------------------------",
                "주문명: " + event.getOrderName(),
                "운송장번호: " + event.getTrackingNumber()
        );

        try {
            kakaoMessageUtil.sendAnnounceDeliveryMessage(
                    event.getOrderName(),
                    event.getTrackingNumber(),
                    event.getOrderNumber(),
                    event.getPhoneNumber()
            );
        } catch (Exception e) {
            log.error("배송 안내 메시지 발송 실패", e);
        }
    }
}