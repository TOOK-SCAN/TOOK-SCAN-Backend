package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestScanMessageEvent {

    private final String orderName;
    private final Long orderId;
    private final String userEmail;
    private final String phoneNumber;

    public static RequestScanMessageEvent of(String orderName, Long orderId, String userEmail, String phoneNumber) {
        return RequestScanMessageEvent.builder()
                .orderName(orderName)
                .orderId(orderId)
                .userEmail(userEmail)
                .phoneNumber(phoneNumber)
                .build();
    }
}