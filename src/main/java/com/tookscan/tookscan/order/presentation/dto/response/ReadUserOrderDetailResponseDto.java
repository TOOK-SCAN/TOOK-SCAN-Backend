package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ECouponType;
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
    private final String id;

    @JsonProperty("order_number")
    @NotNull
    private final String orderNumber;

    @JsonProperty("order_status")
    @NotNull
    private final EOrderStatus orderStatus;

    @JsonProperty("order_date")
    @NotNull
    private final String orderDate;

    @JsonProperty("delivery_expiration_date")
    @NotNull
    private final String deliveryExpirationDate;

    @JsonProperty("payment_expiration_date")
    private final String paymentExpirationDate;

    @JsonProperty("payment_date")
    private final String paymentDate;

    @JsonProperty("phone_number")
    @NotNull
    private final String phoneNumber;

    @JsonProperty("receiver_name")
    @NotNull
    private final String receiverName;

    @JsonProperty("email")
    @NotNull
    private final String email;

    @JsonProperty("address")
    private final AddressResponseDto address;

    @JsonProperty("delivery_request")
    private final String deliveryRequest;

    @JsonProperty("documents")
    @NotNull
    private final List<DocumentInfoDto> documents;

    @JsonProperty("is_one_day_scan")
    @NotNull
    private final Boolean isOneDayScan;

    @JsonProperty("coupon_name")
    private final String couponName;

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

    @JsonProperty("coupon_id")
    private final String couponId;

    @JsonProperty("coupon_type")
    private final ECouponType couponType;

    @JsonProperty("coupon_price")
    private final Integer couponPrice;

    @JsonProperty("coupon_percentage")
    private final Integer couponPercentage;

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

        @JsonProperty("is_ocr_enabled")
        @NotNull
        private final Boolean isOcrEnabled;

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
                               Integer documentPrice,
                               ERecoveryOption recoveryOption,
                               Boolean isOcrEnabled,
                               Integer recoveryPrice,
                               Integer oneDayScanPrice,
                               Integer cuttingPrice,
                               Integer ocrPrice) {
            this.name = name;
            this.pageCount = pageCount;
            this.documentPrice = documentPrice;
            this.recoveryOption = recoveryOption;
            this.isOcrEnabled = isOcrEnabled;
            this.recoveryPrice = recoveryPrice;
            this.oneDayScanPrice = oneDayScanPrice;
            this.cuttingPrice = cuttingPrice;
            this.ocrPrice = ocrPrice;
            this.validateSelf();
        }

        public static DocumentInfoDto fromEntity(Document document) {
            return DocumentInfoDto.builder()
                    .name(document.getName())
                    .pageCount(document.getPageCount())
                    .documentPrice(document.getDocumentPrice())
                    .recoveryOption(document.getRecoveryOption())
                    .isOcrEnabled(document.getIsOcrEnabled())
                    .recoveryPrice(document.getRecoveryOptionPrice())
                    .oneDayScanPrice(document.getOneDayScanPrice())
                    .ocrPrice(document.getOcrPrice())
                    .cuttingPrice(document.getCuttingPrice())
                    .build();
        }
    }

    @Builder
    public ReadUserOrderDetailResponseDto(
            String id,
            String orderNumber,
            EOrderStatus orderStatus,
            String orderDate,
            String deliveryExpirationDate,
            String paymentExpirationDate,
            String paymentDate,
            String phoneNumber,
            String receiverName,
            String email,
            AddressResponseDto address,
            String deliveryRequest,
            List<DocumentInfoDto> documents,
            Boolean isOneDayScan,
            Integer documentsPrice,
            Integer oneDayScanPrice,
            Integer deliveryPrice,
            Integer recoveryPrice,
            Integer cuttingPrice,
            String couponId,
            ECouponType couponType,
            Integer couponPrice,
            Integer couponPercentage,
            Integer paymentTotal,
            String receiptUrl,
            String couponName,
            Integer ocrPrice
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.deliveryExpirationDate = deliveryExpirationDate;
        this.paymentExpirationDate = paymentExpirationDate;
        this.paymentDate = paymentDate;
        this.phoneNumber = phoneNumber;
        this.receiverName = receiverName;
        this.email = email;
        this.address = address;
        this.deliveryRequest = deliveryRequest;
        this.documents = documents;
        this.isOneDayScan = isOneDayScan;
        this.documentsPrice = documentsPrice;
        this.oneDayScanPrice = oneDayScanPrice;
        this.deliveryPrice = deliveryPrice;
        this.recoveryPrice = recoveryPrice;
        this.cuttingPrice = cuttingPrice;
        this.couponId = couponId;
        this.couponType = couponType;
        this.couponPrice = couponPrice;
        this.couponPercentage = couponPercentage;
        this.paymentTotal = paymentTotal;
        this.receiptUrl = receiptUrl;
        this.couponName = couponName;
        this.ocrPrice = ocrPrice;
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

        int ocrPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getOcrPrice)
                .sum();

        int cuttingPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getCuttingPrice)
                .sum();

        int recoveryPriceSum = docs.stream()
                .mapToInt(DocumentInfoDto::getRecoveryPrice)
                .sum();

        String couponName = order.getCoupon() != null
                ? order.getCoupon().getName()
                : null;

        return ReadUserOrderDetailResponseDto.builder()
                .id(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .orderDate(DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()))
                .deliveryExpirationDate(
                        DateTimeUtil.convertLocalDateTimeToDartString(order.getDeliveryExpirationDate()))
                .paymentExpirationDate(
                        order.getPaymentExpirationDate() != null
                                ? DateTimeUtil.convertLocalDateTimeToDartString(order.getPaymentExpirationDate())
                                : null)
                .paymentDate(paymentDate)
                .phoneNumber(order.getDelivery().getPhoneNumber())
                .receiverName(order.getDelivery().getReceiverName())
                .email(order.getDelivery().getEmail())
                .address(order.getDelivery().getAddress() != null
                        ? AddressResponseDto.fromEntity(order.getDelivery().getAddress())
                        : null)
                .deliveryRequest(order.getDelivery().getRequest())
                .documents(docs)
                .isOneDayScan(order.getIsOneDayScan())
                .couponName(couponName)
                .couponId(order.getCoupon() != null ? order.getCoupon().getId().toString() : null)
                .couponType(order.getCoupon() != null ? order.getCoupon().getType() : null)
                .couponPercentage(order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null)
                .documentsPrice(docsPriceSum)
                .oneDayScanPrice(oneDayScanPriceSum)
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .recoveryPrice(recoveryPriceSum)
                .cuttingPrice(cuttingPriceSum)
                .ocrPrice(ocrPriceSum)
                .couponPrice(order.getCoupon() != null ? order.getCoupon().getDiscountPrice() : null)
                .paymentTotal(paymentOpt.map(Payment::getTotalAmount).orElse(order.getTotalAmount()))
                .receiptUrl(paymentOpt.map(Payment::getReceiptUrl).orElse(null))
                .build();
    }
}