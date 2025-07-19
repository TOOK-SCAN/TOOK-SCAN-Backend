package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
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
        // 중복된 이름 검사
        if (order.getDocuments().stream().anyMatch(doc -> doc.getName().equals(name))) {
            throw new CommonException(ErrorCode.DUPLICATE_DOCUMENT_NAME, "문서 이름: " + name);
        }

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
        // 중복된 이름 검사
        if (document.getOrder().getDocuments().stream().anyMatch(doc -> doc.getName().equals(name))) {
            throw new CommonException(ErrorCode.DUPLICATE_DOCUMENT_NAME, "문서 이름: " + name);
        }

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
