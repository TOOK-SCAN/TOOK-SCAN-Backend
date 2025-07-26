package com.tookscan.tookscan.message.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestScanMessageEvent {

    private final String orderName;
    private final String orderNumber;
    private final String userEmail;
    private final String phoneNumber;

    public static RequestScanMessageEvent of(String orderName, String orderNumber, String userEmail, String phoneNumber) {
        return RequestScanMessageEvent.builder()
                .orderName(orderName)
                .orderNumber(orderNumber)
                .userEmail(userEmail)
                .phoneNumber(phoneNumber)
                .build();
    }
}