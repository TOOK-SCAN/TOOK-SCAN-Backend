package com.tookscan.tookscan.message.domain.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AnnounceScanFinishMessageEvent {

    private final String userEmail;
    private final String orderName;
    private final String phoneNumber;

    public static AnnounceScanFinishMessageEvent of(String userEmail, String orderName, String phoneNumber) {
        return AnnounceScanFinishMessageEvent.builder()
                .userEmail(userEmail)
                .orderName(orderName)
                .phoneNumber(phoneNumber)
                .build();
    }
}