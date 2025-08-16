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
                .recoveryOptionPrice(calculateRecoveryOptionPrice(pageCount, recoveryOption))
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
        document.updateRecoveryOptionPrice(calculateRecoveryOptionPrice(pageCount, recoveryOption));
        document.calculateTotalAmount();
    }

    public void updateRecoveryOptionPrice(Document document, Integer recoveryOptionPrice) {
        document.updateRecoveryOptionPrice(recoveryOptionPrice);
        document.calculateTotalAmount();
    }

    private int calculateRecoveryOptionPrice(int pageCount, ERecoveryOption recoveryOption) {
        if (recoveryOption == ERecoveryOption.SPRING) {
            int basePrice = 3000; // 400페이지까지 기본 3,000원
            if (pageCount <= 400) {
                return basePrice;
            }
            // 이후 300페이지 단위로 3,000원씩 추가
            int extraPages = pageCount - 400;
            int increments = (int) Math.ceil(extraPages / 300.0); // 300페이지당 3,000원 (올림)
            return basePrice + (increments * 3000);
        }
        // 기타 옵션은 고정가(현재 0원)
        return recoveryOption.getPrice();
    }
}
