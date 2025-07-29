package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CancelPaymentMessageEvent {

    private final String orderName;
    private final String phoneNumber;

    public static CancelPaymentMessageEvent of(String orderName, String phoneNumber) {
        return CancelPaymentMessageEvent.builder()
                .orderName(orderName)
                .phoneNumber(phoneNumber)
                .build();
    }
}
