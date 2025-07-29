package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AnnounceDeliveryMessageEvent {

    private final String orderName;
    private final String trackingNumber;
    private final Long deliveryId;
    private final String phoneNumber;

    public static AnnounceDeliveryMessageEvent of(String orderName, String trackingNumber, Long deliveryId, String phoneNumber) {
        return AnnounceDeliveryMessageEvent.builder()
                .orderName(orderName)
                .trackingNumber(trackingNumber)
                .deliveryId(deliveryId)
                .phoneNumber(phoneNumber)
                .build();
    }
}