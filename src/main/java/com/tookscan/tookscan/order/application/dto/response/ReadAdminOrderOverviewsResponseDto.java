package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ReadAdminOrderOverviewsResponseDto extends SelfValidating<ReadAdminOrderOverviewsResponseDto> {
    @JsonProperty("orders")
    private final List<OrderOverviewsDto> orders;

    @JsonProperty("page_info")
    private final PageInfoDto pageInfo;

    @Builder
    public ReadAdminOrderOverviewsResponseDto(List<OrderOverviewsDto> orders, PageInfoDto pageInfo) {
        this.orders = orders;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    public static ReadAdminOrderOverviewsResponseDto of(List<Order> orders, Page<Long> pageInfo) {
        return ReadAdminOrderOverviewsResponseDto.builder()
                .orders(orders.stream().map(OrderOverviewsDto::fromEntity).toList())
                .pageInfo(PageInfoDto.fromEntity(pageInfo))
                .build();
    }

    public static class OrderOverviewsDto extends SelfValidating<OrderOverviewsDto> {
        @JsonProperty("order_id")
        private final Long orderId;

        @JsonProperty("order_number")
        private final String orderNumber;

        @JsonProperty("name")
        private final String name;

        @JsonProperty("order_status")
        private final EOrderStatus orderStatus;

        @JsonProperty("payment_amount")
        private final Integer paymentAmount;

        @JsonProperty("payment_method")
        private final EPaymentMethod paymentMethod;

        @JsonProperty("easy_payment_provider")
        private final EEasyPaymentProvider easyPaymentProvider;

        @JsonProperty("order_date")
        private final String orderDate;

        @JsonProperty("payment_date")
        private final String paymentDate;

        @JsonProperty("documents")
        private final DocumentsDto documents;

        @Builder
        public OrderOverviewsDto(Long orderId, String orderNumber, String name, EOrderStatus orderStatus,
                                 Integer paymentAmount, EPaymentMethod paymentMethod,
                                 EEasyPaymentProvider easyPaymentProvider,
                                 String orderDate, String paymentDate, DocumentsDto documents) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.name = name;
            this.orderStatus = orderStatus;
            this.paymentAmount = paymentAmount;
            this.paymentMethod = paymentMethod;
            this.easyPaymentProvider = easyPaymentProvider;
            this.orderDate = orderDate;
            this.paymentDate = paymentDate;
            this.documents = documents;
            this.validateSelf();
        }

        public static OrderOverviewsDto fromEntity(Order order) {
            return OrderOverviewsDto.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .name(order.isByUser() ? order.getUser().getName() : order.getDelivery().getReceiverName())
                    .orderStatus(order.getOrderStatus())
                    .paymentAmount(
                            order.getPayment() == null ? null : order.getPayment().getTotalAmount())
                    .paymentMethod(order.getPayment() == null ? null : order.getPayment().getMethod())
                    .easyPaymentProvider(order.getPayment() == null ? null
                            : order.getPayment().getEasyPaymentProvider())
                    .orderDate(DateTimeUtil.convertLocalDateToString(order.getCreatedAt().toLocalDate()))
                    .paymentDate(order.getPayment() == null ? null
                            : DateTimeUtil.convertLocalDateToString(order.getPayment().getApprovedAt().toLocalDate()))
                    .documents(DocumentsDto.fromEntities(order.getDocuments()))
                    .build();
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
                                .map(document -> DocumentsDto.DocumentDto.builder()
                                        .name(document.getName())
                                        .pageCount(document.getPageCount())
                                        .recoveryOption(document.getRecoveryOption().getDescription())
                                        .price(document.calculatePrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            }
        }
    }
}