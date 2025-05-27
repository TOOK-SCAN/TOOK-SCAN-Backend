package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EstimateUserOrderPriceResponseDto extends SelfValidating<EstimateUserOrderPriceResponseDto> {
    @JsonProperty("documents")
    @NotNull
    private final List<DocumentInfoDto> documents;

    @JsonProperty("documents_price")
    private final Integer documentsPrice;

    @JsonProperty("one_day_scan_price")
    private final Integer oneDayScanPrice;

    @JsonProperty("delivery_price")
    private final Integer deliveryPrice;

    @JsonProperty("recovery_price")
    private final Integer recoveryPrice;

    @JsonProperty("cutting_price")
    private final Integer cuttingPrice;

    @JsonProperty("coupon_type")
    private final ECouponType couponType;

    @JsonProperty("coupon_price")
    private final Integer couponPrice;

    @JsonProperty("coupon_percentage")
    private final Integer couponPercentage;

    @JsonProperty("payment_total")
    private final Integer paymentTotal;

    @Getter
    @Valid
    public static class DocumentInfoDto extends SelfValidating<DocumentInfoDto> {
        @JsonProperty("name")
        @NotNull
        private final String name;

        @JsonProperty("page_count")
        @NotNull
        private final Integer pageCount;

        @JsonProperty("document_price")
        @NotNull
        private final Integer documentPrice;

        @JsonProperty("recovery_option")
        @NotNull
        private final ERecoveryOption recoveryOption;

        @JsonProperty("recovery_price")
        @NotNull
        private final Integer recoveryPrice;

        @JsonProperty("one_day_scan_price")
        @NotNull
        private final Integer oneDayScanPrice;

        @JsonProperty("cutting_price")
        @NotNull
        private final Integer cuttingPrice;

        @Builder
        public DocumentInfoDto(String name,
                               Integer pageCount,
                               Integer documentPrice,
                               ERecoveryOption recoveryOption,
                               Integer recoveryPrice,
                               Integer oneDayScanPrice,
                               Integer cuttingPrice) {
            this.name = name;
            this.pageCount = pageCount;
            this.documentPrice = documentPrice;
            this.recoveryOption = recoveryOption;
            this.recoveryPrice = recoveryPrice;
            this.oneDayScanPrice = oneDayScanPrice;
            this.cuttingPrice = cuttingPrice;
            this.validateSelf();
        }

        public static DocumentInfoDto fromEntity(Document document) {
            return DocumentInfoDto.builder()
                    .name(document.getName())
                    .pageCount(document.getPageCount())
                    .documentPrice(document.calculateDocumentPrice())
                    .recoveryOption(document.getRecoveryOption())
                    .recoveryPrice(document.getRecoveryOption().getPrice())
                    .oneDayScanPrice(document.calculateOneDayScanPrice() - document.calculateDocumentPrice())
                    .cuttingPrice(document.getPricePolicy().getDefaultPrice())
                    .build();
        }
    }

    @Builder
    public EstimateUserOrderPriceResponseDto(
            List<DocumentInfoDto> documents,
            Integer documentsPrice,
            Integer oneDayScanPrice,
            Integer deliveryPrice,
            Integer recoveryPrice,
            Integer cuttingPrice,
            ECouponType couponType,
            Integer couponPrice,
            Integer couponPercentage,
            Integer paymentTotal
    ) {
        this.documents = documents;
        this.documentsPrice = documentsPrice;
        this.oneDayScanPrice = oneDayScanPrice;
        this.deliveryPrice = deliveryPrice;
        this.recoveryPrice = recoveryPrice;
        this.cuttingPrice = cuttingPrice;
        this.couponType = couponType;
        this.couponPrice = couponPrice;
        this.couponPercentage = couponPercentage;
        this.paymentTotal = paymentTotal;
        this.validateSelf();
    }

    public static EstimateUserOrderPriceResponseDto fromEntity(Order order) {
        List<DocumentInfoDto> docs = order.getDocuments().stream()
                .map(DocumentInfoDto::fromEntity)
                .toList();

        int docsPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getDocumentPrice)
                .sum();

        int oneDayScanPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getOneDayScanPrice)
                .sum();

        int cuttingPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getCuttingPrice)
                .sum();

        int recoveryPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getRecoveryPrice)
                .sum();

        return EstimateUserOrderPriceResponseDto.builder()
                .documents(docs)
                .couponType(order.getCoupon() != null ? order.getCoupon().getType() : null)
                .couponPercentage(order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null)
                .documentsPrice(docsPriceSum)
                .oneDayScanPrice(oneDayScanPriceSum)
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .recoveryPrice(recoveryPriceSum)
                .cuttingPrice(cuttingPriceSum)
                .couponPrice(order.getCoupon() != null ? order.getCoupon().getDiscountPrice() : null)
                .paymentTotal(order.getTotalAmount())
                .build();
    }
}
