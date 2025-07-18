package com.tookscan.tookscan.mail.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendPdfEmailEvent {

    String email;
    String userName;
    String orderNumber;
    String orderName;
    String pdfUrl;

    public static SendPdfEmailEvent of(String email, String userName, String orderNumber, String orderName, String pdfUrl) {
        return SendPdfEmailEvent.builder()
                .email(email)
                .userName(userName)
                .orderNumber(orderNumber)
                .orderName(orderName)
                .pdfUrl(pdfUrl)
                .build();
    }
}
