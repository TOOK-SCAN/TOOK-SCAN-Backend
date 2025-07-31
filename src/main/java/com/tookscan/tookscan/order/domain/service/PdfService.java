package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.order.domain.Pdf;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfService {

    /**
     * Pdf 엔티티의 URL을 업데이트하는 메서드
     *
     * @param pdf    업데이트할 Pdf 객체
     * @param newUrl 새로운 URL
     */
    public void updatePdfUrl(Pdf pdf, String newUrl) {
        pdf.updatePdfUrl(newUrl);
    }
}
