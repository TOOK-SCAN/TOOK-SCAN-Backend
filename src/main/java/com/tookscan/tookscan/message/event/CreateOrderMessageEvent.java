package com.tookscan.tookscan.message.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateOrderMessageEvent {

    private final String userName;
    private final String userPhone;
    private final String orderName;
    private final String phoneNumber;

    public static CreateOrderMessageEvent of(String userName, String userPhone, String orderName, String phoneNumber) {
        return CreateOrderMessageEvent.builder()
                .userName(userName)
                .userPhone(userPhone)
                .orderName(orderName)
                .phoneNumber(phoneNumber)
                .build();
    }
}