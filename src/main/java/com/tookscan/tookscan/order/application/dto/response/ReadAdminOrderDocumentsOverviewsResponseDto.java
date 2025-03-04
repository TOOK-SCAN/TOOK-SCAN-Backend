package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.InitialDocument;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

public class ReadAdminOrderDocumentsOverviewsResponseDto extends
        SelfValidating<ReadAdminOrderDocumentsOverviewsResponseDto> {

    @JsonProperty("order_status")
    @NotNull
    private final EOrderStatus orderStatus;

    @JsonProperty("order_number")
    @NotBlank
    private final String orderNumber;

    @JsonProperty("order_memo")
    private final String orderMemo;

    @JsonProperty("initial_documents")
    @NotNull
    private final List<InitialDocumentDto> initialDocumentDtos;

    @JsonProperty("real_documents")
    @NotNull
    private final List<RealDocumentDto> realDocumentDtos;

    @JsonProperty("payment_info")
    @NotNull
    private final PaymentInfoDto paymentInfoDto;

    @Builder
    public ReadAdminOrderDocumentsOverviewsResponseDto(EOrderStatus orderStatus, String orderNumber, String orderMemo,
                                                       List<InitialDocumentDto> initialDocumentDtos,
                                                       List<RealDocumentDto> realDocumentDtos,
                                                       PaymentInfoDto paymentInfoDto) {
        this.orderStatus = orderStatus;
        this.orderNumber = orderNumber;
        this.orderMemo = orderMemo;
        this.initialDocumentDtos = initialDocumentDtos;
        this.realDocumentDtos = realDocumentDtos;
        this.paymentInfoDto = paymentInfoDto;
        this.validateSelf();
    }

    public static ReadAdminOrderDocumentsOverviewsResponseDto of(Order order, Map<Long, EScanStatus> scanStatuses) {
        return ReadAdminOrderDocumentsOverviewsResponseDto.builder()
                .orderStatus(order.getOrderStatus())
                .orderNumber(order.getOrderNumber())
                .orderMemo(order.getMemo())
                .initialDocumentDtos(order.getInitialDocuments().stream()
                        .map(InitialDocumentDto::fromEntity)
                        .toList())
                .realDocumentDtos(order.getDocuments().stream()
                        .map(document -> RealDocumentDto.of(document, scanStatuses.get(document.getId())))
                        .toList())
                .paymentInfoDto(PaymentInfoDto.fromEntity(order))
                .build();
    }

    @Getter
    public static class InitialDocumentDto extends SelfValidating<InitialDocumentDto> {

        @JsonProperty("id")
        @NotNull
        private final Long id;

        @JsonProperty("name")
        @NotBlank
        private final String name;

        @JsonProperty("page_count")
        @NotNull
        private final Integer pageCount;

        @JsonProperty("recovery_option")
        @NotNull
        private final ERecoveryOption recoveryOption;

        @JsonProperty("price")
        @NotNull
        private final Integer price;

        @Builder
        public InitialDocumentDto(Long id, String name, Integer pageCount, ERecoveryOption recoveryOption,
                                  Integer price) {
            this.id = id;
            this.name = name;
            this.pageCount = pageCount;
            this.recoveryOption = recoveryOption;
            this.price = price;
            this.validateSelf();
        }

        public static InitialDocumentDto fromEntity(InitialDocument initialDocument) {
            return InitialDocumentDto.builder()
                    .id(initialDocument.getId())
                    .name(initialDocument.getName())
                    .pageCount(initialDocument.getPageCount())
                    .recoveryOption(initialDocument.getRecoveryOption())
                    .price(initialDocument.calculatePrice())
                    .build();
        }
    }

    @Getter
    public static class RealDocumentDto extends SelfValidating<RealDocumentDto> {

        @JsonProperty("id")
        @NotNull
        private final Long id;

        @JsonProperty("name")
        @NotBlank
        private final String name;

        @JsonProperty("page_count")
        @NotNull
        private final Integer pageCount;

        @JsonProperty("recovery_option")
        @NotNull
        private final ERecoveryOption recoveryOption;

        @JsonProperty("price")
        @NotNull
        private final Integer price;

        @JsonProperty("additional_price")
        @NotNull
        private final Integer additionalPrice;

        @JsonProperty("scan_status")
        @NotNull
        private final EScanStatus scanStatus;

        @Builder
        public RealDocumentDto(Long id, String name, Integer pageCount, ERecoveryOption recoveryOption,
                               Integer price, Integer additionalPrice, EScanStatus scanStatus) {
            this.id = id;
            this.name = name;
            this.pageCount = pageCount;
            this.recoveryOption = recoveryOption;
            this.price = price;
            this.additionalPrice = additionalPrice;
            this.scanStatus = scanStatus;
            this.validateSelf();
        }

        public static RealDocumentDto of(Document document, EScanStatus scanStatus) {
            return RealDocumentDto.builder()
                    .id(document.getId())
                    .name(document.getName())
                    .pageCount(document.getPageCount())
                    .recoveryOption(document.getRecoveryOption())
                    .price(document.calculatePrice())
                    .additionalPrice(document.getAdditionalPrice())
                    .scanStatus(scanStatus)
                    .build();
        }
    }

    @Getter
    public static class PaymentInfoDto extends SelfValidating<PaymentInfoDto> {

        @JsonProperty("documents_price")
        @NotNull
        private final Integer documentsPrice;

        @JsonProperty("delivery_price")
        @NotNull
        private final Integer deliveryPrice;

        @JsonProperty("total_price")
        @NotNull
        private final Integer totalPrice;

        @Builder
        public PaymentInfoDto(Integer documentsPrice, Integer deliveryPrice, Integer totalPrice) {
            this.documentsPrice = documentsPrice;
            this.deliveryPrice = deliveryPrice;
            this.totalPrice = totalPrice;
            this.validateSelf();
        }

        public static PaymentInfoDto fromEntity(Order order) {
            return PaymentInfoDto.builder()
                    .documentsPrice(order.getDocumentsTotalAmount())
                    .deliveryPrice(order.getDelivery().getDeliveryPrice())
                    .totalPrice(order.getTotalAmount())
                    .build();
        }
    }
}

