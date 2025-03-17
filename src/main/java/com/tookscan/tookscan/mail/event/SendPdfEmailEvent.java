package com.tookscan.tookscan.mail.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendPdfEmailEvent {

    String email;
    String orderName;
    String pdfUrl;

    public static SendPdfEmailEvent of(String email, String orderName, String pdfUrl) {
        return SendPdfEmailEvent.builder()
                .email(email)
                .orderName(orderName)
                .pdfUrl(pdfUrl)
                .build();
    }
}
