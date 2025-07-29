package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestPaymentMessageEvent {

    private final String orderName;
    private final Integer orderPrice;
    private final Long orderId;
    private final String paymentKey;
    private final String orderNumber;
    private final String phoneNumber;

    public static RequestPaymentMessageEvent of(String orderName, Integer orderPrice, Long orderId, String paymentKey, String orderNumber, String phoneNumber) {
        return RequestPaymentMessageEvent.builder()
                .orderName(orderName)
                .orderPrice(orderPrice)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .orderNumber(orderNumber)
                .phoneNumber(phoneNumber)
                .build();
    }
}