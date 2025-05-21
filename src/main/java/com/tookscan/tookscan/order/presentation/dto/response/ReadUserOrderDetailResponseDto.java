package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.payment.domain.Payment;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserOrderDetailResponseDto extends SelfValidating<ReadUserOrderDetailResponseDto> {
    @JsonProperty("id")
    @NotNull
    private final Long id;

    @JsonProperty("order_number")
    @NotNull
    private final String orderNumber;

    @JsonProperty("order_status")
    @NotNull
    private final EOrderStatus orderStatus;

    @JsonProperty("order_date")
    @NotNull
    private final String orderDate;

    @JsonProperty("payment_expiration_date")
    private final String paymentExpirationDate;

    @JsonProperty("payment_date")
    private final String paymentDate;

    @JsonProperty("phone_number")
    @NotNull
    private final String phoneNumber;

    @JsonProperty("email")
    @NotNull
    private final String email;

    @JsonProperty("zone_code")
    @NotNull
    private final String zoneCode;

    @JsonProperty("address")
    @NotNull
    private final String address;

    @JsonProperty("address_detail")
    private final String addressDetail;

    @JsonProperty("delivery_request")
    private final String deliveryRequest;

    @JsonProperty("documents")
    @NotNull
    private final List<DocumentInfoDto> documents;

    @JsonProperty("coupon_name")
    private final String couponName;

    @JsonProperty("documents_price")
    private final Integer documentsPrice;

    @JsonProperty("one_day_scan_price")
    private final Integer oneDayScanPrice;

    @JsonProperty("delivery_price")
    private final Integer deliveryPrice;

    @JsonProperty("cutting_price")
    private final Integer cuttingPrice;

    @JsonProperty("coupon_price")
    private final Integer couponPrice;

    @JsonProperty("payment_total")
    private final Integer paymentTotal;

    @JsonProperty("receipt_url")
    private final String receiptUrl;

    @Getter
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
                    .oneDayScanPrice(document.calculateOneDayScanPrice())
                    .cuttingPrice(document.getPricePolicy().getDefaultPrice())
                    .build();
        }
    }

    @Builder
    public ReadUserOrderDetailResponseDto(
            Long id,
            String orderNumber,
            EOrderStatus orderStatus,
            String orderDate,
            String paymentExpirationDate,
            String paymentDate,
            String phoneNumber,
            String email,
            String zoneCode,
            String address,
            String addressDetail,
            String deliveryRequest,
            List<DocumentInfoDto> documents,
            Integer documentsPrice,
            Integer oneDayScanPrice,
            Integer deliveryPrice,
            Integer cuttingPrice,
            Integer couponPrice,
            Integer paymentTotal,
            String receiptUrl,
            String couponName
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.paymentExpirationDate = paymentExpirationDate;
        this.paymentDate = paymentDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.zoneCode = zoneCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.deliveryRequest = deliveryRequest;
        this.documents = documents;
        this.documentsPrice = documentsPrice;
        this.oneDayScanPrice = oneDayScanPrice;
        this.deliveryPrice = deliveryPrice;
        this.cuttingPrice = cuttingPrice;
        this.couponPrice = couponPrice;
        this.paymentTotal = paymentTotal;
        this.receiptUrl = receiptUrl;
        this.couponName = couponName;
        this.validateSelf();
    }

    public static ReadUserOrderDetailResponseDto fromEntity(Order order) {
        Optional<Payment> paymentOpt = Optional.ofNullable(order.getPayment());

        String paymentDate = paymentOpt
                .map(Payment::getCreatedAt)
                .map(DateTimeUtil::convertLocalDateTimeToDartString)
                .orElse(null);

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

        String couponName = order.getCoupon() != null
                ? order.getCoupon().getName()
                : null;

        return ReadUserOrderDetailResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .orderDate(DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()))
                .paymentExpirationDate(
                        order.getPaymentExpirationDate() != null
                                ? DateTimeUtil.convertLocalDateTimeToDartString(order.getPaymentExpirationDate())
                                : null)
                .paymentDate(paymentDate)
                .phoneNumber(order.getDelivery().getPhoneNumber())
                .email(order.getDelivery().getEmail())
                .zoneCode(order.getDelivery().getAddress().getZoneCode())
                .address(order.getDelivery().getAddress().getFullAddress())
                .addressDetail(order.getDelivery().getAddress().getAddressDetail())
                .deliveryRequest(order.getDelivery().getRequest())
                .documents(docs)
                .couponName(couponName)
                .documentsPrice(docsPriceSum)
                .oneDayScanPrice(oneDayScanPriceSum)
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .cuttingPrice(cuttingPriceSum)
                .couponPrice(order.getCoupon() != null ? order.getDiscountAmount() : null)
                .paymentTotal(paymentOpt.map(Payment::getTotalAmount).orElse(order.getTotalAmount()))
                .receiptUrl(paymentOpt.map(Payment::getReceiptUrl).orElse(null))
                .build();
    }
}