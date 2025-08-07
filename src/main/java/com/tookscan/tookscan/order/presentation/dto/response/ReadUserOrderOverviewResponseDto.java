package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.payment.domain.Payment;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ReadUserOrderOverviewResponseDto extends SelfValidating<ReadUserOrderOverviewResponseDto> {

    @JsonProperty("status_count")
    @NotNull
    private final StatusCountDto statusCount;

    @JsonProperty("orders")
    @NotNull
    private final List<OrderInfoDto> orders;

    @JsonProperty("customer_key")
    @NotNull
    private final UUID customerKey;

    @JsonProperty("page_info")
    @NotNull
    private final PageInfoDto pageInfo;

    @Getter
    public static class StatusCountDto {
        @JsonProperty("scan_waiting")
        private final long scanWaiting;

        @JsonProperty("scan_in_progress")
        private final long scanInProgress;

        @JsonProperty("scan_completed")
        private final long scanCompleted;

        @Builder
        public StatusCountDto(long scanWaiting, long scanInProgress, long scanCompleted) {
            this.scanWaiting = scanWaiting;
            this.scanInProgress = scanInProgress;
            this.scanCompleted = scanCompleted;
        }
    }

    @Getter
    public static class DocumentDto {
        @JsonProperty("name")
        private final String name;

        @JsonProperty("page_count")
        private final Integer pageCount;

        @JsonProperty("recovery_option")
        private final ERecoveryOption recoveryOption;

        @JsonProperty("is_ocr_enabled")
        private final Boolean isOcrEnabled;

        @Builder
        public DocumentDto(String name, Integer pageCount, ERecoveryOption recoveryOption, Boolean isOcrEnabled) {
            this.name = name;
            this.pageCount = pageCount;
            this.recoveryOption = recoveryOption;
            this.isOcrEnabled = isOcrEnabled;
        }
    }

    @Getter
    public static class OrderInfoDto extends SelfValidating<OrderInfoDto> {
        @JsonProperty("id")
        @NotNull
        private final String id;

        @JsonProperty("order_date")
        @NotNull
        private final String orderDate;

        @JsonProperty("order_status")
        @NotNull
        private final EOrderStatus orderStatus;

        @JsonProperty("order_number")
        @NotNull
        private final String orderNumber;

        @JsonProperty("documents")
        @NotNull
        private final List<DocumentDto> documents;

        @JsonProperty("payment_total")
        private final Integer paymentTotal;

        @JsonProperty("delivery_expiration_date")
        @NotNull
        private final String deliveryExpirationDate;

        @JsonProperty("payment_expiration_date")
        private final String paymentExpirationDate;

        @JsonProperty("is_delivery")
        @NotNull
        private final Boolean isDelivery;

        @JsonProperty("delivery_info")
        @NotNull
        private final DeliveryInfoDto deliveryInfo;

        @Getter
        public static class DeliveryInfoDto {
            @JsonProperty("receiver_name")
            private final String receiverName;

            @JsonProperty("phone_number")
            private final String phoneNumber;

            @JsonProperty("email")
            private final String email;

            @JsonProperty("tracking_number")
            private final String trackingNumber;

            @Builder
            public DeliveryInfoDto(String receiverName, String phoneNumber, String email, String trackingNumber) {
                this.receiverName = receiverName;
                this.phoneNumber = phoneNumber;
                this.email = email;
                this.trackingNumber = trackingNumber;
            }
        }

        @Builder
        public OrderInfoDto(String id,
                            String orderDate,
                            EOrderStatus orderStatus,
                            String orderNumber,
                            List<DocumentDto> documents,
                            Integer paymentTotal,
                            String deliveryExpirationDate,
                            String paymentExpirationDate,
                            Boolean isDelivery,
                            DeliveryInfoDto deliveryInfo) {
            this.id = id;
            this.deliveryInfo = deliveryInfo;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.orderNumber = orderNumber;
            this.documents = documents;
            this.paymentTotal = paymentTotal;
            this.deliveryExpirationDate = deliveryExpirationDate;
            this.paymentExpirationDate = paymentExpirationDate;
            this.isDelivery = isDelivery;
            this.validateSelf();
        }

        public static OrderInfoDto fromEntity(Order order) {
            Optional<Payment> paymentOpt = Optional.ofNullable(order.getPayment());

            Integer paymentTotal = paymentOpt.map(Payment::getTotalAmount)
                    .orElse(order.getTotalAmount());

            String deliveryExpirationDate = DateTimeUtil.convertLocalDateTimeToDartStringWithoutSecond(
                    order.getDeliveryExpirationDate());

            String paymentExpiration = Optional.ofNullable(order.getPaymentExpirationDate())
                    .map(DateTimeUtil::convertLocalDateTimeToDartStringWithoutSecond)
                    .orElse(null);

            List<DocumentDto> docs = order.getDocuments().stream()
                    .map(doc -> DocumentDto.builder()
                            .name(doc.getName())
                            .pageCount(doc.getPageCount())
                            .recoveryOption(doc.getRecoveryOption())
                            .isOcrEnabled(doc.getIsOcrEnabled())
                            .build())
                    .toList();

            return OrderInfoDto.builder()
                    .id(order.getId().toString())
                    .deliveryInfo(DeliveryInfoDto.builder()
                            .receiverName(order.getDelivery().getReceiverName())
                            .phoneNumber(order.getDelivery().getPhoneNumber())
                            .email(order.getDelivery().getEmail())
                            .trackingNumber(order.getDelivery().getTrackingNumber() != null ?
                                    order.getDelivery().getTrackingNumber() : null)
                            .build())
                    .orderDate(DateTimeUtil.convertLocalDateToDartString(order.getCreatedAt().toLocalDate()))
                    .orderStatus(order.getOrderStatus())
                    .orderNumber(order.getOrderNumber())
                    .documents(docs)
                    .paymentTotal(paymentTotal)
                    .deliveryExpirationDate(deliveryExpirationDate)
                    .paymentExpirationDate(paymentExpiration)
                    .isDelivery(order.isDelivery())
                    .build();
        }
    }

    @Builder
    public ReadUserOrderOverviewResponseDto(StatusCountDto statusCount,
                                            List<OrderInfoDto> orders,
                                            UUID customerKey,
                                            PageInfoDto pageInfo) {
        this.statusCount = statusCount;
        this.orders = orders;
        this.customerKey = customerKey;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    public static ReadUserOrderOverviewResponseDto of(Page<Order> orders, Integer scanWaitingCount,
                                                      Integer scanInProgressCount, Integer scanCompletedCount, UUID customerKey) {

        StatusCountDto statusCount = StatusCountDto.builder()
                .scanWaiting(scanWaitingCount)
                .scanInProgress(scanInProgressCount)
                .scanCompleted(scanCompletedCount)
                .build();

        List<OrderInfoDto> orderDtos = orders.stream()
                .map(OrderInfoDto::fromEntity)
                .toList();

        return ReadUserOrderOverviewResponseDto.builder()
                .statusCount(statusCount)
                .orders(orderDtos)
                .customerKey(customerKey)
                .pageInfo(PageInfoDto.fromEntity(orders))
                .build();
    }
}
