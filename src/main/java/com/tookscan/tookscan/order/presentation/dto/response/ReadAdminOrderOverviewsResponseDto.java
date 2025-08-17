package com.tookscan.tookscan.order.presentation.dto.response;

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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ReadAdminOrderOverviewsResponseDto extends SelfValidating<ReadAdminOrderOverviewsResponseDto> {
    @JsonProperty("orders_info")
    private final OrderOverviewsInfoDto ordersInfo;

    @JsonProperty("orders")
    private final List<OrderOverviewsDto> orders;

    @JsonProperty("page_info")
    private final PageInfoDto pageInfo;

    @Builder
    public ReadAdminOrderOverviewsResponseDto(List<OrderOverviewsDto> orders, PageInfoDto pageInfo,
                                              OrderOverviewsInfoDto ordersInfo) {
        this.ordersInfo = ordersInfo;
        this.orders = orders;
        this.pageInfo = pageInfo;
        this.validateSelf();
    }

    public static ReadAdminOrderOverviewsResponseDto of(List<Order> filteredOrders, Map<EOrderStatus, Long> statusCounts, Map<EOrderStatus, Long> overallStatusCounts, Page<Long> pageInfo) {

        List<Long> orderIds = pageInfo.getContent();

        Map<Long, Order> orderMap = filteredOrders.stream()
                .collect(Collectors.toMap(Order::getId, Function.identity()));

        List<Order> sortedOrders = orderIds.stream()
                .map(orderMap::get)
                .toList();
        OrderOverviewsInfoDto ordersInfo = OrderOverviewsInfoDto.fromStatusCounts(sortedOrders, statusCounts, overallStatusCounts);

        return ReadAdminOrderOverviewsResponseDto.builder()
                .orders(sortedOrders.stream().map(OrderOverviewsDto::fromEntity).toList())
                .pageInfo(PageInfoDto.fromEntity(pageInfo))
                .ordersInfo(ordersInfo)
                .build();
    }

    public static class OrderOverviewsInfoDto extends SelfValidating<OrderOverviewsInfoDto> {
        @JsonProperty("total_count")
        private final Integer totalCount;

        @JsonProperty("apply_completed_count")
        private final Integer applyCompletedCount;

        @JsonProperty("company_arrived_count")
        private final Integer companyArrivedCount;

        @JsonProperty("payment_waiting_count")
        private final Integer paymentWaitingCount;

        @JsonProperty("payment_completed_count")
        private final Integer paymentCompletedCount;

        @JsonProperty("scan_in_progress_count")
        private final Integer scanInProgressCount;

        @JsonProperty("scan_completed_count")
        private final Integer scanCompletedCount;

        @JsonProperty("recovery_in_progress_count")
        private final Integer recoveryInProgressCount;

        @JsonProperty("post_waiting_count")
        private final Integer postWaitingCount;

        @JsonProperty("all_completed_count")
        private final Integer allCompletedCount;

        @JsonProperty("cancel_count")
        private final Integer cancelCount;

        @JsonProperty("overall_total_count")
        private final Integer overallTotalCount;

        @JsonProperty("overall_in_progress_count")
        private final Integer overallInProgressCount;

        @JsonProperty("overall_completed_count")
        private final Integer overallCompletedCount;

        @Builder
        public OrderOverviewsInfoDto(Integer applyCompletedCount, Integer companyArrivedCount,
                                     Integer paymentWaitingCount,
                                     Integer paymentCompletedCount, Integer scanInProgressCount,
                                     Integer scanCompletedCount, Integer recoveryInProgressCount,
                                     Integer postWaitingCount, Integer allCompletedCount, Integer cancelCount,
                                     Integer totalCount, Integer overallTotalCount, Integer overallInProgressCount,
                                     Integer overallCompletedCount) {
            this.totalCount = totalCount;
            this.applyCompletedCount = applyCompletedCount;
            this.companyArrivedCount = companyArrivedCount;
            this.paymentWaitingCount = paymentWaitingCount;
            this.paymentCompletedCount = paymentCompletedCount;
            this.scanInProgressCount = scanInProgressCount;
            this.scanCompletedCount = scanCompletedCount;
            this.recoveryInProgressCount = recoveryInProgressCount;
            this.postWaitingCount = postWaitingCount;
            this.allCompletedCount = allCompletedCount;
            this.cancelCount = cancelCount;
            this.overallTotalCount = overallTotalCount;
            this.overallInProgressCount = overallInProgressCount;
            this.overallCompletedCount = overallCompletedCount;
            this.validateSelf();
        }

        public static OrderOverviewsInfoDto fromStatusCounts(List<Order> currentPageOrders, Map<EOrderStatus, Long> statusCounts, Map<EOrderStatus, Long> overallStatusCounts) {
            return OrderOverviewsInfoDto.builder()
                    .totalCount(currentPageOrders.size())
                    .applyCompletedCount(getStatusCount(statusCounts, EOrderStatus.APPLY_COMPLETED))
                    .companyArrivedCount(getStatusCount(statusCounts, EOrderStatus.COMPANY_ARRIVED))
                    .paymentWaitingCount(getStatusCount(statusCounts, EOrderStatus.PAYMENT_WAITING))
                    .paymentCompletedCount(getStatusCount(statusCounts, EOrderStatus.PAYMENT_COMPLETED))
                    .scanInProgressCount(getStatusCount(statusCounts, EOrderStatus.SCAN_IN_PROGRESS))
                    .scanCompletedCount(getStatusCount(statusCounts, EOrderStatus.SCAN_COMPLETED))
                    .recoveryInProgressCount(getStatusCount(statusCounts, EOrderStatus.RECOVERY_IN_PROGRESS))
                    .postWaitingCount(getStatusCount(statusCounts, EOrderStatus.POST_WAITING))
                    .allCompletedCount(getStatusCount(statusCounts, EOrderStatus.ALL_COMPLETED))
                    .cancelCount(getStatusCount(statusCounts, EOrderStatus.CANCEL))
                    .overallTotalCount(calculateTotalCount(overallStatusCounts))
                    .overallInProgressCount(calculateInProgressCount(overallStatusCounts))
                    .overallCompletedCount(calculateCompletedCount(overallStatusCounts))
                    .build();
        }
        
        private static Integer calculateTotalCount(Map<EOrderStatus, Long> statusCounts) {
            return Math.toIntExact(statusCounts.values().stream().mapToLong(Long::longValue).sum());
        }
        
        private static Integer calculateInProgressCount(Map<EOrderStatus, Long> statusCounts) {
            long cancelCount = statusCounts.getOrDefault(EOrderStatus.CANCEL, 0L);
            long allCompletedCount = statusCounts.getOrDefault(EOrderStatus.ALL_COMPLETED, 0L);
            long totalCount = statusCounts.values().stream().mapToLong(Long::longValue).sum();
            return Math.toIntExact(totalCount - cancelCount - allCompletedCount);
        }
        
        private static Integer calculateCompletedCount(Map<EOrderStatus, Long> statusCounts) {
            long cancelCount = statusCounts.getOrDefault(EOrderStatus.CANCEL, 0L);
            long allCompletedCount = statusCounts.getOrDefault(EOrderStatus.ALL_COMPLETED, 0L);
            return Math.toIntExact(cancelCount + allCompletedCount);
        }

        private static Integer getStatusCount(Map<EOrderStatus, Long> statusCounts, EOrderStatus status) {
            return Math.toIntExact(statusCounts.getOrDefault(status, 0L));
        }
    }


    public static class OrderOverviewsDto extends SelfValidating<OrderOverviewsDto> {
        @JsonProperty("order_id")
        private final String orderId;

        @JsonProperty("order_number")
        private final String orderNumber;

        @JsonProperty("name")
        private final String name;

        @JsonProperty("order_status")
        private final EOrderStatus orderStatus;

        @JsonProperty("predicted_price")
        private final Integer predictedPrice;

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

        @JsonProperty("pdf_send_date")
        private final String pdfSendDate;

        @JsonProperty("is_one_day_scan")
        private final Boolean isOneDayScan;

        @JsonProperty("has_recovery_option")
        private final Boolean hasRecoveryOption;

        @JsonProperty("tracking_number")
        private final String trackingNumber;

        @JsonProperty("is_as_in_progress")
        private final Boolean isAsInProgress;

        @JsonProperty("memo")
        private final String memo;

        @JsonProperty("documents")
        private final DocumentsDto documents;

        @Builder
        public OrderOverviewsDto(String orderId, String orderNumber, String name, EOrderStatus orderStatus,
                                 Integer paymentAmount, EPaymentMethod paymentMethod,
                                 EEasyPaymentProvider easyPaymentProvider,
                                 String orderDate, String paymentDate, DocumentsDto documents, Integer predictedPrice,
                                 String pdfSendDate, Boolean isOneDayScan, Boolean hasRecoveryOption,
                                 String trackingNumber, Boolean isAsInProgress, String memo) {
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
            this.predictedPrice = predictedPrice;
            this.pdfSendDate = pdfSendDate;
            this.isOneDayScan = isOneDayScan;
            this.hasRecoveryOption = hasRecoveryOption;
            this.trackingNumber = trackingNumber;
            this.isAsInProgress = isAsInProgress;
            this.memo = memo;
            this.validateSelf();
        }

        public static OrderOverviewsDto fromEntity(Order order) {
            return OrderOverviewsDto.builder()
                    .orderId(order.getId().toString())
                    .orderNumber(order.getOrderNumber())
                    .name(order.getDelivery().getReceiverName())
                    .orderStatus(order.getOrderStatus())
                    .paymentAmount(
                            order.getPayment() == null ? null : order.getPayment().getTotalAmount())
                    .paymentMethod(order.getPayment() == null ? null : order.getPayment().getMethod())
                    .easyPaymentProvider(order.getPayment() == null ? null
                            : order.getPayment().getEasyPaymentProvider())
                    .orderDate(DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()))
                    .paymentDate(order.getPayment() == null ? null
                            : DateTimeUtil.convertLocalDateTimeToDartString(
                                    order.getPayment().getApprovedAt()))
                    .documents(DocumentsDto.fromEntities(order.getDocuments()))
                    .predictedPrice(order.getTotalAmount())
                    .pdfSendDate(order.getPdfSendDate() == null ? null
                            : DateTimeUtil.convertLocalDateTimeToDartString(
                                    order.getPdfSendDate()))
                    .isOneDayScan(order.getIsOneDayScan())
                    .hasRecoveryOption(order.isDelivery())
                    .trackingNumber(order.getDelivery().getTrackingNumber() == null ? null
                            : order.getDelivery().getTrackingNumber())
                    .isAsInProgress(order.getIsAsInProgress())
                    .memo(order.getMemo() == null ? null : order.getMemo())
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

                @JsonProperty("is_ocr_enabled")
                private final Boolean isOcrEnabled;

                @Builder
                public DocumentDto(String name, Integer pageCount, String recoveryOption, Integer price,
                                   Boolean isOcrEnabled) {
                    this.name = name;
                    this.pageCount = pageCount;
                    this.recoveryOption = recoveryOption;
                    this.price = price;
                    this.isOcrEnabled = isOcrEnabled;
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
                                        .price(document.getDocumentPrice())
                                        .isOcrEnabled(document.getIsOcrEnabled())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
            }
        }
    }
}