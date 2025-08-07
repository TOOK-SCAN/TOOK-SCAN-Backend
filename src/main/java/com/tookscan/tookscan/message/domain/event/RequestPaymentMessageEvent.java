package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class RequestPaymentMessageEvent {

    private final String orderName;
    private final Integer orderPrice;
    private final Long orderId;
    private final String orderNumber;
    private final String phoneNumber;
    private final String email;
    private final String userName;
    private final UUID customerKey;

    public static RequestPaymentMessageEvent of(String orderName, Integer orderPrice, Long orderId, String orderNumber, String phoneNumber, String email, String userName, UUID customerKey) {
        return RequestPaymentMessageEvent.builder()
                .orderName(orderName)
                .orderPrice(orderPrice)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .phoneNumber(phoneNumber)
                .email(email)
                .userName(userName)
                .customerKey(customerKey)
                .build();
    }
}