package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.payment.domain.Payment;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @JsonProperty("coupon_description")
    private final String couponDescription;

    @JsonProperty("coupon_start_date")
    private final String couponStartDate;

    @JsonProperty("coupon_end_date")
    private final String couponEndDate;

    @JsonProperty("payment_total")
    private final Integer paymentTotal;

    @JsonProperty("receipt_url")
    private final String receiptUrl;

    @JsonProperty("customer_key")
    private final UUID customerKey;

    @JsonProperty("is_delivery")
    private final Boolean isDelivery;

    @JsonProperty("tracking_number")
    private final String trackingNumber;

    @JsonProperty("carrier_name")
    private final String carrierName;

    @Getter
    public static class DocumentInfoDto extends SelfValidating<DocumentInfoDto> {

        @JsonProperty("id")
        private final String id;

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
                               Integer pagePrice,
                               Integer documentPrice,
                               ERecoveryOption recoveryOption,
                               Boolean isOcrEnabled,
                               Integer recoveryPrice,
                               Integer oneDayScanPrice,
                               Integer cuttingPrice,
                               Integer ocrPrice,
                               String id
        ) {
            this.name = name;
            this.pageCount = pageCount;
            this.pagePrice = pagePrice;
            this.documentPrice = documentPrice;
            this.recoveryOption = recoveryOption;
            this.isOcrEnabled = isOcrEnabled;
            this.recoveryPrice = recoveryPrice;
            this.oneDayScanPrice = oneDayScanPrice;
            this.cuttingPrice = cuttingPrice;
            this.ocrPrice = ocrPrice;
            this.id = id;
            this.validateSelf();
        }

        public static DocumentInfoDto fromEntity(Document document) {
            return DocumentInfoDto.builder()
                    .name(document.getName())
                    .pageCount(document.getPageCount())
                    .pagePrice(document.getPagePrice())
                    .documentPrice(document.getDocumentPrice())
                    .recoveryOption(document.getRecoveryOption())
                    .isOcrEnabled(document.getIsOcrEnabled())
                    .recoveryPrice(document.getRecoveryOptionPrice())
                    .oneDayScanPrice(document.getOneDayScanPrice())
                    .ocrPrice(document.getOcrPrice())
                    .cuttingPrice(document.getCuttingPrice())
                    .id(document.getId().toString())
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
            String couponDescription,
            String couponStartDate,
            String couponEndDate,
            Integer paymentTotal,
            String receiptUrl,
            String couponName,
            Integer ocrPrice,
            UUID customerKey,
            Boolean isDelivery,
            String trackingNumber,
            String carrierName
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
        this.couponDescription = couponDescription;
        this.couponStartDate = couponStartDate;
        this.couponEndDate = couponEndDate;
        this.paymentTotal = paymentTotal;
        this.receiptUrl = receiptUrl;
        this.couponName = couponName;
        this.ocrPrice = ocrPrice;
        this.customerKey = customerKey;
        this.isDelivery = isDelivery;
        this.trackingNumber = trackingNumber;
        this.carrierName = carrierName;
        this.validateSelf();
    }

    public static ReadUserOrderDetailResponseDto of(Order order, UUID customerKey) {
        Optional<Payment> paymentOpt = Optional.ofNullable(order.getPayment());

        String paymentDate = paymentOpt
                .map(Payment::getCreatedAt)
                .map(DateTimeUtil::convertLocalDateTimeToDartStringWithoutSecond)
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

        String couponName = order.getUsedCoupon() != null
                ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getName()
                : null;

        String description = null;
        if (order.getUsedCoupon() != null) {
            CouponTemplate couponTemplate = order.getUsedCoupon().getIssuedCoupon().getCouponTemplate();

            if (couponTemplate.getType().equals(ECouponType.PERCENTAGE)) {
                int percent = couponTemplate.getDiscountPercent();
                Integer minPrice = couponTemplate.getMinOrderPrice();
                Integer maxPrice = couponTemplate.getMaxDiscountPrice();

                if (minPrice != null && maxPrice != null) {
                    description = String.format("%d%% 할인 (%d원 이상 / 최대 %d원)", percent, minPrice, maxPrice);
                } else if (maxPrice != null) {
                    description = String.format("%d%% 할인 (최대 %d원)", percent, maxPrice);
                } else if (minPrice != null) {
                    description = String.format("%d%% 할인 (%d원 이상)", percent, minPrice);
                } else {
                    description = String.format("%d%% 할인", percent);
                }

            } else if (couponTemplate.getType().equals(ECouponType.AMOUNT)) {
                int amount = couponTemplate.getDiscountPrice();
                Integer minPrice = couponTemplate.getMinOrderPrice();
                Integer maxPrice = couponTemplate.getMaxDiscountPrice();

                if (minPrice != null && maxPrice != null) {
                    description = String.format("%d원 할인 (%d원 이상 / 최대 %d원)", amount, minPrice, maxPrice);
                } else if (maxPrice != null) {
                    description = String.format("%d원 할인 (최대 %d원)", amount, maxPrice);
                } else if (minPrice != null) {
                    description = String.format("%d원 할인 (%d원 이상)", amount, minPrice);
                } else {
                    description = String.format("%d원 할인", amount);
                }
            } else if (couponTemplate.getType().equals(ECouponType.OCR_FREE)) {
                description = ECouponType.OCR_FREE.getDescription();
            } else if (couponTemplate.getType().equals(ECouponType.DELIVERY_PRICE_FREE)) {
                description = ECouponType.DELIVERY_PRICE_FREE.getDescription();
            }
        }

        return ReadUserOrderDetailResponseDto.builder()
                .id(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .orderDate(DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(order.getCreatedAt()))
                .deliveryExpirationDate(
                        DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(order.getDeliveryExpirationDate()))
                .paymentExpirationDate(
                        order.getPaymentExpirationDate() != null
                                ? DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(
                                order.getPaymentExpirationDate())
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
                .couponId(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getId().toString() : null)
                .couponType(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getType() : null)
                .couponPercentage(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getDiscountPercent() : null)
                .documentsPrice(docsPriceSum)
                .oneDayScanPrice(oneDayScanPriceSum)
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .recoveryPrice(recoveryPriceSum)
                .cuttingPrice(cuttingPriceSum)
                .ocrPrice(ocrPriceSum)
                .couponPrice(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getDiscountPrice(order.getDocumentsTotalAmount(), ocrPriceSum, order.getDelivery().getDeliveryPrice()) : null)
                .couponDescription(description)
                .couponStartDate(order.getUsedCoupon() != null && order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getStartDateTime() != null
                        ? DateTimeUtil.convertLocalDateToDartString(order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getStartDateTime().toLocalDate())
                        : null)
                .couponEndDate(order.getUsedCoupon() != null && order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getEndDateTime() != null
                        ? DateTimeUtil.convertLocalDateToDartString(order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getEndDateTime().toLocalDate())
                        : null)
                .paymentTotal(paymentOpt.map(Payment::getTotalAmount).orElse(order.getTotalAmount()))
                .receiptUrl(paymentOpt.map(Payment::getReceiptUrl).orElse(null))
                .customerKey(customerKey)
                .isDelivery(order.isDelivery())
                .trackingNumber(
                        order.getDelivery().getTrackingNumber() != null ? order.getDelivery().getTrackingNumber()
                                : null)
                .carrierName("한진택배")
                .build();
    }
}