package com.tookscan.tookscan.order.domain.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminPdfUploadRequestedEvent {
    private final Long pdfId;
    private final Long documentId;
    private final String tempFilePath;
    private final String originalFileName;
    private final String storedFileName;
    private final String userName;
    private final String userPhone;
    private final String orderNumber;
    private final String orderCreatedAt;
    private final Long orderId;
    private final byte[] aesKey;
}

