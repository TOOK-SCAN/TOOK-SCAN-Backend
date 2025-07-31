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
import java.util.stream.Stream;
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

    @JsonProperty("ocr_price")
    private final Integer ocrPrice;

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

        @JsonProperty("page_price")
        @NotNull
        private final Integer pagePrice;

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

        @JsonProperty("ocr_price")
        @NotNull
        private final Integer ocrPrice;

        @JsonProperty("cutting_price")
        @NotNull
        private final Integer cuttingPrice;

        @Builder
        public DocumentInfoDto(String name,
                               Integer pageCount,
                               Integer pagePrice,
                               Integer documentPrice,
                               ERecoveryOption recoveryOption,
                               Integer recoveryPrice,
                               Integer oneDayScanPrice,
                               Integer cuttingPrice,
                               Integer ocrPrice) {
            this.name = name;
            this.pageCount = pageCount;
            this.pagePrice = pagePrice;
            this.documentPrice = documentPrice;
            this.recoveryOption = recoveryOption;
            this.recoveryPrice = recoveryPrice;
            this.oneDayScanPrice = oneDayScanPrice;
            this.ocrPrice = ocrPrice;
            this.cuttingPrice = cuttingPrice;
            this.validateSelf();
        }

        public static DocumentInfoDto fromEntity(Document document) {
            return DocumentInfoDto.builder()
                    .name(document.getName())
                    .pageCount(document.getPageCount())
                    .pagePrice(document.getPagePrice())
                    .documentPrice(document.getDocumentPrice())
                    .recoveryOption(document.getRecoveryOption())
                    .recoveryPrice(document.getRecoveryOptionPrice())
                    .oneDayScanPrice(document.getOneDayScanPrice())
                    .ocrPrice(document.getOcrPrice())
                    .cuttingPrice(document.getCuttingPrice())
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
            Integer paymentTotal,
            Integer ocrPrice
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
        this.ocrPrice = ocrPrice;
        this.validateSelf();
    }

    public static EstimateUserOrderPriceResponseDto of(Order order,
                                                       List<Document> unCheckedDocuments) {
        List<DocumentInfoDto> checkedDocs = order.getDocuments().stream()
                .map(DocumentInfoDto::fromEntity)
                .toList();

        List<DocumentInfoDto> uncheckedDocs = unCheckedDocuments.stream()
                .map(DocumentInfoDto::fromEntity)
                .toList();

        List<DocumentInfoDto> allDocs = Stream
                .concat(checkedDocs.stream(), uncheckedDocs.stream())
                .toList();

        int docsPriceSum = checkedDocs.stream()
                .mapToInt(DocumentInfoDto::getDocumentPrice)
                .sum();
        int oneDayScanPriceSum = checkedDocs.stream()
                .mapToInt(DocumentInfoDto::getOneDayScanPrice)
                .sum();
        int ocrPriceSum = checkedDocs.stream()
                .mapToInt(DocumentInfoDto::getOcrPrice)
                .sum();
        int cuttingPriceSum = checkedDocs.stream()
                .mapToInt(DocumentInfoDto::getCuttingPrice)
                .sum();
        int recoveryPriceSum = checkedDocs.stream()
                .mapToInt(DocumentInfoDto::getRecoveryPrice)
                .sum();

        return EstimateUserOrderPriceResponseDto.builder()
                .documents(allDocs)
                .couponType(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getType() : null)
                .couponPercentage(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getDiscountPercent() : null)
                .documentsPrice(docsPriceSum)
                .oneDayScanPrice(oneDayScanPriceSum)
                .ocrPrice(ocrPriceSum)
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .recoveryPrice(recoveryPriceSum)
                .cuttingPrice(cuttingPriceSum)
                .couponPrice(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getDiscountPrice(order.getDocumentsTotalAmount(), ocrPriceSum, order.getDelivery().getDeliveryPrice()) : 0)
                .paymentTotal(order.getTotalAmount())
                .build();
    }
}
