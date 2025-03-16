package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReadAdminDeliveriesSummariesResponseDto extends SelfValidating<ReadAdminDeliveriesSummariesResponseDto> {

    @JsonProperty("orders")
    private final List<OrderDeliveryResponseDto> orders;

    @JsonProperty("page_info")
    private final PageInfoDto pageInfo;

    @Builder
    public ReadAdminDeliveriesSummariesResponseDto(List<OrderDeliveryResponseDto> orders, PageInfoDto pageInfo) {
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

        @JsonProperty("documents")
        private final DocumentsDto documents;

        @Builder
        public OrderDeliveryResponseDto(String orderId, String orderNumber, String name, String phoneNumber,
                                        EOrderStatus status, DocumentsDto documents) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.documents = documents;
        }

        @Getter
        public static class DocumentsDto extends SelfValidating<DocumentsDto> {

            @JsonProperty("total_count")
            private final Integer totalCount;

            @JsonProperty("documents")
            private final List<DocumentDto> documents;

            @Builder
            public DocumentsDto(Integer totalCount, List<DocumentDto> documents) {
                this.totalCount = totalCount;
                this.documents = documents;
                this.validateSelf();
            }

            @Getter
            public static class DocumentDto extends SelfValidating<DocumentDto> {

                @JsonProperty("name")
                private final String name;

                @JsonProperty("page_count")
                private final Integer pageCount;

                @JsonProperty("recovery_option")
                private final String recoveryOption;

                @JsonProperty("price")
                private final Integer price;

                @Builder
                public DocumentDto(String name, Integer pageCount, String recoveryOption, Integer price) {
                    this.name = name;
                    this.pageCount = pageCount;
                    this.recoveryOption = recoveryOption;
                    this.price = price;
                    this.validateSelf();
                }
            }

            public static DocumentsDto fromEntities(List<Document> documents) {
                return DocumentsDto.builder()
                        .totalCount(documents.size())
                        .documents(documents.stream()
                                .map(document -> DocumentDto.builder()
                                        .name(document.getName())
                                        .pageCount(document.getPageCount())
                                        .recoveryOption(document.getRecoveryOption().getDescription())
                                        .price(document.calculatePrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            }
        }

        public static OrderDeliveryResponseDto fromEntity(Order order) {
            return OrderDeliveryResponseDto.builder()
                    .orderId(order.getId().toString())
                    .orderNumber(order.getOrderNumber())
                    .name(order.isByUser() ? order.getUser().getName() : order.getDelivery().getReceiverName())
                    .phoneNumber(
                            order.isByUser() ? order.getUser().getPhoneNumber() : order.getDelivery().getPhoneNumber())
                    .status(order.getOrderStatus())
                    .documents(DocumentsDto.fromEntities(order.getDocuments()))
                    .build();
        }
    }

    public static ReadAdminDeliveriesSummariesResponseDto of(List<Order> orders, Page<Long> orderIds) {
        return ReadAdminDeliveriesSummariesResponseDto.builder()
                .orders(orders.stream()
                        .map(OrderDeliveryResponseDto::fromEntity)
                        .toList())
                .pageInfo(PageInfoDto.fromEntity(orderIds))
                .build();
    }

}
