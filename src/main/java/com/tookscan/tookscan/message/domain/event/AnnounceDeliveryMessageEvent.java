package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AnnounceDeliveryMessageEvent {

    private final String orderName;
    private final String trackingNumber;
    private final String orderNumber;
    private final String phoneNumber;

    public static AnnounceDeliveryMessageEvent of(String orderName, String trackingNumber, String orderNumber, String phoneNumber) {
        return AnnounceDeliveryMessageEvent.builder()
                .orderName(orderName)
                .trackingNumber(trackingNumber)
                .orderNumber(orderNumber)
                .phoneNumber(phoneNumber)
                .build();
    }
}