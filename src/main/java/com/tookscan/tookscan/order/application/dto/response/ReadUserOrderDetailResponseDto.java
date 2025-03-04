package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserOrderDetailResponseDto extends SelfValidating<ReadUserOrderDetailResponseDto> {
    @JsonProperty("id")
    @NotNull
    private final Long orderId;

    @JsonProperty("order_number")
    @NotNull
    private final String orderNumber;

    @JsonProperty("order_status")
    @NotNull
    private final EOrderStatus orderStatus;

    @JsonProperty("order_date")
    @NotNull
    private final String orderDate;

    @JsonProperty("receiver_name")
    @NotNull
    private final String receiverName;

    @JsonProperty("tracking_number")
    private final String trackingNumber;

    @JsonProperty("delivery_price")
    private final Integer deliveryPrice;

    @JsonProperty("address")
    @NotNull
    private final String address;

    @JsonProperty("document_description")
    @NotNull
    private final String documentDescription;

    @JsonProperty("documents")
    @NotNull
    private final List<DocumentInfoDto> documents;

    @JsonProperty("payment_method")
    private final EPaymentMethod paymentMethod;

    @JsonProperty("easy_payment_provider")
    private final EEasyPaymentProvider easyPaymentProvider;

    @JsonProperty("payment_total")
    private final Integer paymentTotal;

    @JsonProperty("user_info")
    private final UserInfoDto userInfo;

    @Getter
    public static class DocumentInfoDto extends SelfValidating<DocumentInfoDto> {
        @JsonProperty("name")
        @NotNull
        private final String name;

        @JsonProperty("page")
        @NotNull
        private final Integer page;

        @JsonProperty("price")
        @NotNull
        private final Integer price;

        @JsonProperty("recovery_option")
        @NotNull
        private final ERecoveryOption recoveryOption;

        @Builder
        public DocumentInfoDto(String name, Integer page, Integer price, ERecoveryOption recoveryOption) {
            this.name = name;
            this.page = page;
            this.price = price;
            this.recoveryOption = recoveryOption;
            this.validateSelf();
        }

        public static DocumentInfoDto fromEntity(Document document) {
            return DocumentInfoDto.builder()
                    .name(document.getName())
                    .page(document.getPageCount())
                    .price(document.calculatePrice())
                    .recoveryOption(document.getRecoveryOption())
                    .build();
        }
    }

    @Getter
    public static class UserInfoDto extends SelfValidating<UserInfoDto> {

        @JsonProperty("name")
        @NotNull
        private final String name;

        @JsonProperty("email")
        @NotNull
        private final String email;

        @JsonProperty("phone_number")
        @NotNull
        private final String phoneNumber;

        @Builder
        public UserInfoDto(String name, String phoneNumber, String email) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.validateSelf();
        }
    }

    @Builder
    public ReadUserOrderDetailResponseDto(
            Long orderId,
            String orderNumber,
            EOrderStatus orderStatus,
            String orderDate,
            String receiverName,
            String trackingNumber,
            String address,
            String documentDescription,
            List<DocumentInfoDto> documents,
            EPaymentMethod paymentMethod,
            EEasyPaymentProvider easyPaymentProvider,
            Integer paymentTotal,
            Integer deliveryPrice,
            UserInfoDto userInfo
    ) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.receiverName = receiverName;
        this.trackingNumber = trackingNumber;
        this.address = address;
        this.documentDescription = documentDescription;
        this.documents = documents;
        this.paymentMethod = paymentMethod;
        this.easyPaymentProvider = easyPaymentProvider;
        this.paymentTotal = paymentTotal;
        this.deliveryPrice = deliveryPrice;
        this.userInfo = userInfo;
        this.validateSelf();
    }

    public static ReadUserOrderDetailResponseDto fromEntity(Order order) {

        Optional<Payment> payment = Optional.ofNullable(order.getPayment());

        EPaymentMethod paymentMethod = payment.map(Payment::getMethod).orElse(null);
        EEasyPaymentProvider easyPaymentProvider = payment.map(Payment::getEasyPaymentProvider).orElse(null);
        Integer paymentTotal = payment.map(Payment::getTotalAmount).orElse(order.getDocumentsTotalAmount());

        if (order.getDocuments().stream()
                .anyMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD)) {
            paymentTotal += order.getDelivery().getDeliveryPrice();
        }

        return ReadUserOrderDetailResponseDto.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus().toDisplayString())
                .orderDate(DateTimeUtil.convertLocalDateTimeToKORString(order.getCreatedAt()))
                .receiverName(order.getDelivery().getReceiverName())
                .trackingNumber(order.getDelivery().getTrackingNumber())
                .address(order.getDelivery().getAddress().getFullAddress())
                .documentDescription(order.getDocumentsDescription())
                .documents(order.getDocuments().stream()
                        .map(DocumentInfoDto::fromEntity)
                        .toList())
                .paymentMethod(paymentMethod)
                .easyPaymentProvider(easyPaymentProvider)
                .paymentTotal(paymentTotal)
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
                .userInfo(UserInfoDto.builder()
                        .name(order.getDelivery().getReceiverName())
                        .phoneNumber(order.getDelivery().getPhoneNumber())
                        .email(order.getDelivery().getEmail())
                        .build())
                .build();
    }
}
