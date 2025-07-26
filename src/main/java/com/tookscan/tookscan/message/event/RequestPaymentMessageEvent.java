package com.tookscan.tookscan.message.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestPaymentMessageEvent {

    private final String orderName;
    private final Integer orderPrice;
    private final String orderNumber;
    private final String phoneNumber;

    public static RequestPaymentMessageEvent of(String orderName, Integer orderPrice, String orderNumber, String phoneNumber) {
        return RequestPaymentMessageEvent.builder()
                .orderName(orderName)
                .orderPrice(orderPrice)
                .orderNumber(orderNumber)
                .phoneNumber(phoneNumber)
                .build();
    }
}