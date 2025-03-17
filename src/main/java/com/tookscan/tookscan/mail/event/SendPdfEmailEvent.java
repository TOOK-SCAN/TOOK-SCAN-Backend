package com.tookscan.tookscan.mail.event;

import com.tookscan.tookscan.core.dto.PdfFileDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SendPdfEmailEvent {

    String email;
    String orderName;
    String pdfUrls;

    public static SendPdfEmailEvent of(String email, String orderName, String pdfUrls) {
        return SendPdfEmailEvent.builder()
                .email(email)
                .orderName(orderName)
                .pdfUrls(pdfUrls)
                .build();
    }
}
