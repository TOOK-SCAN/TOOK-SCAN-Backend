package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ReadAdminDeliveriesOverviewsResponseDto extends SelfValidating<ReadAdminDeliveriesOverviewsResponseDto> {

    @JsonProperty("orders")
    private final List<OrderDeliveryResponseDto> orders;

    @JsonProperty("page_info")
    private final PageInfoDto pageInfo;

    @Builder
    public ReadAdminDeliveriesOverviewsResponseDto(List<OrderDeliveryResponseDto> orders, PageInfoDto pageInfo) {
        this.orders = orders;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    public static class OrderDeliveryResponseDto {
        @JsonProperty("order_id")
        private final String orderId;

        @JsonProperty("order_number")
        private final String orderNumber;

        @JsonProperty("name")
        private final String name;

        @JsonProperty("phone_number")
        private final String phoneNumber;

        @JsonProperty("status")
        private final EOrderStatus status;

        @JsonProperty("payment_amount")
        private final Integer paymentAmount;

        @JsonProperty("address")
        private final AddressResponseDto address;

        @JsonProperty("delivery_id")
        private final String deliveryId;

        @Builder
        public OrderDeliveryResponseDto(String orderId, String orderNumber, String name, String phoneNumber,
                                        EOrderStatus status, Integer paymentAmount, AddressResponseDto address,
                                        String deliveryId) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.paymentAmount = paymentAmount;
            this.address = address;
            this.deliveryId = deliveryId;
        }

        public static OrderDeliveryResponseDto fromEntity(Order order) {
            return OrderDeliveryResponseDto.builder()
                    .orderId(order.getId().toString())
                    .orderNumber(order.getOrderNumber())
                    .name(order.isByUser() ? order.getUser().getName() : order.getDelivery().getReceiverName())
                    .phoneNumber(
                            order.isByUser() ? order.getUser().getPhoneNumber() : order.getDelivery().getPhoneNumber())
                    .status(order.getOrderStatus())
                    .paymentAmount(order.getPayment() != null ? order.getPayment().getTotalAmount() : null)
                    .address(AddressResponseDto.fromEntity(order.getDelivery().getAddress()))
                    .deliveryId(order.getDelivery().getId().toString())
                    .build();
        }
    }

    public static ReadAdminDeliveriesOverviewsResponseDto of(List<Order> orders, Page<Long> orderIds) {
        return ReadAdminDeliveriesOverviewsResponseDto.builder()
                .orders(orders.stream()
                        .map(OrderDeliveryResponseDto::fromEntity)
                        .toList())
                .pageInfo(PageInfoDto.fromEntity(orderIds))
                .build();
    }
}
