package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminOrderSummariesResponseDto extends SelfValidating<ReadAdminOrderSummariesResponseDto> {

    @JsonProperty("orders")
    private final List<OrderSummaryDto> orders;

    @JsonProperty("page_info")
    private final PageInfoDto pageInfoDto;

    @Builder
    public ReadAdminOrderSummariesResponseDto(List<OrderSummaryDto> orders, PageInfoDto pageInfoDto) {
        this.orders = orders;
        this.pageInfoDto = pageInfoDto;
        this.validateSelf();
    }

    @Getter
    public static class OrderSummaryDto extends SelfValidating<OrderSummaryDto> {

        @JsonProperty("id")
        private final Long id;

        @JsonProperty("order_number")
        private final String orderNumber;

        @JsonProperty("name")
        private final String name;

        @JsonProperty("phone_number")
        private final String phoneNumber;

        @JsonProperty("status")
        private final EOrderStatus status;

        @JsonProperty("predicted_price")
        private final Integer predictedPrice;

        @JsonProperty("apply_date")
        private final String applyDate;

        @JsonProperty("documents")
        private final DocumentsDto documents;

        @Builder
        public OrderSummaryDto(Long id, String orderNumber, String name, String phoneNumber, EOrderStatus orderStatus, Integer predictedPrice,
                               String applyDate, DocumentsDto documents) {
            this.id = id;
            this.orderNumber = orderNumber;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.status = orderStatus;
            this.predictedPrice = predictedPrice;
            this.applyDate = applyDate;
            this.documents = documents;
            this.validateSelf();
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

        public static OrderSummaryDto fromEntity(Order order) {
            return OrderSummaryDto.builder()
                    .id(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .name(order.isByUser() ? order.getUser().getName() : order.getDelivery().getReceiverName())
                    .phoneNumber(order.isByUser() ? order.getUser().getPhoneNumber() : order.getDelivery().getPhoneNumber())
                    .orderStatus(order.getOrderStatus())
                    .predictedPrice(order.getTotalAmount())
                    .applyDate(DateTimeUtil.convertLocalDateToString(LocalDate.from(order.getCreatedAt())))
                    .documents(DocumentsDto.fromEntities(order.getDocuments()))
                    .build();
        }
    }

    public static ReadAdminOrderSummariesResponseDto of(List<Order> orders, PageInfoDto pageInfoDto) {
        return ReadAdminOrderSummariesResponseDto.builder()
                .orders(orders.stream()
                        .map(OrderSummaryDto::fromEntity)
                        .toList())
                .pageInfoDto(pageInfoDto)
                .build();
    }
}
