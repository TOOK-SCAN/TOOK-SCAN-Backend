package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    public Document createDocument(
            String name,
            int pageCount,
            ERecoveryOption recoveryOption,
            Order order,
            PricePolicy pricePolicy
    ) {
        return Document.builder()
                .name(name)
                .pageCount(pageCount)
                .recoveryOption(recoveryOption)
                .order(order)
                .pricePolicy(pricePolicy)
                .additionalPrice(0)
                .scanStatus(EScanStatus.UNABLE)
                .build();
    }

    public void updateDocument(
            Document document,
            String name,
            int pageCount,
            ERecoveryOption recoveryOption,
            int additionalPrice,
            EScanStatus scanStatus
    ) {
        document.updateName(name);
        document.updatePageCount(pageCount);
        document.updateRecoveryOption(recoveryOption);
        document.updateAdditionalPrice(additionalPrice);
        document.updateScanStatus(scanStatus);
    }
}
