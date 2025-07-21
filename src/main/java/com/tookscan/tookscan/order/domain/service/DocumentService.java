package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    public Document createDocument(
            String name,
            int pageCount,
            ERecoveryOption recoveryOption,
            Order order,
            Integer cuttingPrice,
            Integer defaultPricePerPage,
            Integer additionalPriceForOcr,
            Boolean isOcrEnabled
    ) {

        Document document = Document.builder()
                .name(name)
                .pageCount(pageCount)
                .recoveryOption(recoveryOption)
                .order(order)
                .cuttingPrice(cuttingPrice)
                .defaultPricePerPage(defaultPricePerPage)
                .recoveryOptionPrice(recoveryOption.getPrice())
                .isOcrEnabled(isOcrEnabled)
                .additionalPriceForOcr(additionalPriceForOcr)
                .totalAmount(0) // 초기 총액은 0으로 설정
                .build();
        document.calculateTotalAmount();
        return document;
    }

    public void updateDocument(
            Document document,
            String name,
            int pageCount,
            ERecoveryOption recoveryOption,
            boolean isOcrEnabled,
            Integer additionalPriceForOcr
    ) {

        document.updateName(name);
        document.updatePageCount(pageCount);
        document.updateRecoveryOption(recoveryOption);
        document.updateOcrEnabled(isOcrEnabled, additionalPriceForOcr);
        document.calculateTotalAmount();
    }

    public void updateRecoveryOptionPrice(Document document, Integer recoveryOptionPrice) {
        document.updateRecoveryOptionPrice(recoveryOptionPrice);
        document.calculateTotalAmount();
    }
}
