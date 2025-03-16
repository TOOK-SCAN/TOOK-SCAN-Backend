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
    List<PdfFileDto> pdfFiles;

    public static SendPdfEmailEvent of(String email, String orderName, List<PdfFileDto> pdfFiles) {
        return SendPdfEmailEvent.builder()
                .email(email)
                .orderName(orderName)
                .pdfFiles(pdfFiles)
                .build();
    }
}
