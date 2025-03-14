package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ReadUserOrderOverviewResponseDto extends SelfValidating<ReadUserOrderOverviewResponseDto> {

    @JsonProperty("orders")
    @NotNull
    private final List<OrderInfoDto> orders;

    @JsonProperty("page_info")
    @NotNull
    private final PageInfoDto pageInfo;

    @Getter
    public static class OrderInfoDto extends SelfValidating<OrderInfoDto> {
        @JsonProperty("id")
        @NotNull
        private final String orderId;

        @JsonProperty("order_status")
        @NotNull
        private final EOrderStatus orderStatus;

        @JsonProperty("document_description")
        @NotNull
        private final String documentDescription;

        @JsonProperty("order_number")
        @NotNull
        private final String orderNumber;

        @JsonProperty("order_date")
        @NotNull
        private final String orderDate;

        @JsonProperty("receiver_name")
        @NotNull
        private final String receiverName;

        @JsonProperty("address")
        @NotNull
        private final String address;

        @JsonProperty("payment_method")
        private final EPaymentMethod paymentMethod;

        @JsonProperty("easy_payment_provider")
        private final EEasyPaymentProvider easyPaymentProvider;

        @JsonProperty("payment_total")
        private final Integer paymentTotal;

        @JsonProperty("is_delivery")
        private final Boolean isDelivery;

        @Builder
        public OrderInfoDto(String orderId, EOrderStatus orderStatus, String documentDescription, String orderNumber,
                            String orderDate, String receiverName, String address, EPaymentMethod paymentMethod,
                            EEasyPaymentProvider easyPaymentProvider, Integer paymentTotal, Boolean isDelivery) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.documentDescription = documentDescription;
            this.orderNumber = orderNumber;
            this.orderDate = orderDate;
            this.receiverName = receiverName;
            this.address = address;
            this.paymentMethod = paymentMethod;
            this.easyPaymentProvider = easyPaymentProvider;
            this.paymentTotal = paymentTotal;
            this.isDelivery = isDelivery;
            this.validateSelf();
        }

        public static OrderInfoDto fromEntity(Order order) {
            Optional<Payment> payment = Optional.ofNullable(order.getPayment());

            EPaymentMethod paymentMethod = payment.map(Payment::getMethod).orElse(null);
            EEasyPaymentProvider easyPaymentProvider = payment.map(Payment::getEasyPaymentProvider).orElse(null);
            Integer paymentTotal = payment.map(Payment::getTotalAmount).orElse(order.getTotalAmount());

            return OrderInfoDto.builder()
                    .orderId(order.getId().toString())
                    .orderStatus(order.getOrderStatus().toDisplayString())
                    .documentDescription(order.getDocumentsDescription())
                    .orderNumber(order.getOrderNumber())
                    .orderDate(DateTimeUtil.convertLocalDateTimeToKORString(order.getCreatedAt()))
                    .receiverName(order.getDelivery().getReceiverName())
                    .address(order.getDelivery().getAddress().getFullAddress())
                    .paymentMethod(paymentMethod)
                    .easyPaymentProvider(easyPaymentProvider)
                    .paymentTotal(paymentTotal)
                    .isDelivery(order.isDelivery())
                    .build();
        }
    }

    @Builder
    public ReadUserOrderOverviewResponseDto(List<OrderInfoDto> orders, PageInfoDto pageInfo) {
        this.orders = orders;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    public static ReadUserOrderOverviewResponseDto fromEntity(Page<Order> orders) {

        if (orders.isEmpty()) {
            return ReadUserOrderOverviewResponseDto.builder()
                    .orders(List.of())
                    .pageInfo(PageInfoDto.fromEntity(orders))
                    .build();
        }

        return ReadUserOrderOverviewResponseDto.builder()
                .orders(orders.stream()
                        .map(OrderInfoDto::fromEntity)
                        .toList())
                .pageInfo(PageInfoDto.fromEntity(orders))
                .build();
    }

}