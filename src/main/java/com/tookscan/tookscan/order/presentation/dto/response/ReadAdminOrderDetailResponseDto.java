package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.InitialDocument;
import com.tookscan.tookscan.order.domain.InitialOrder;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderDetailResponseDto.InitialOrderDto.PaymentInfoDto;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminOrderDetailResponseDto extends
        SelfValidating<ReadAdminOrderDetailResponseDto> {

    @JsonProperty("order_number")
    @NotBlank
    private final String orderNumber;

    @JsonProperty("order_status")
    @NotNull
    private final EOrderStatus orderStatus;

    @JsonProperty("is_as_in_progress")
    @NotNull
    private final Boolean isAsInProgress;

    @JsonProperty("created_at")
    @NotBlank
    private final String createdAt;

    @JsonProperty("arrived_at")
    @NotBlank
    private final String arrivedAt;

    @JsonProperty("user")
    @NotNull
    private final UserDto userDto;

    @JsonProperty("documents")
    @NotNull
    private final List<DocumentDto> documentDtos;

    @JsonProperty("initial_order")
    private final InitialOrderDto initialOrderDto;

    @JsonProperty("payment_info")
    @NotNull
    private final PaymentInfoDto paymentInfoDto;

    @JsonProperty("order_memo")
    private final String orderMemo;

    @JsonProperty("tracking_number")
    private final String trackingNumber;

    @JsonProperty("tracking_number_registered_at")
    private final String trackingNumberRegisteredAt;

    @JsonProperty("recovery_completed_at")
    private final String recoveryCompletedAt;

    @JsonProperty("cancelled_at")
    private final String orderCancelledAt;

    @JsonProperty("cancel_reason")
    private final String cancelReason;

    @JsonProperty("pdf_sended_at")
    private final String pdfSendedAt;

    @JsonProperty("paymented_at")
    private final String paymentedAt;

    @JsonProperty("payment_method")
    private final EPaymentMethod paymentMethod;

    @JsonProperty("payment_provider")
    private final EEasyPaymentProvider paymentProvider;

    @JsonProperty("payment_receipt_url")
    private final String paymentReceiptUrl;

    @Builder
    public ReadAdminOrderDetailResponseDto(
            String orderNumber,
            EOrderStatus orderStatus,
            Boolean isAsInProgress,
            String createdAt,
            String arrivedAt,
            UserDto userDto,
            List<DocumentDto> documentDtos,
            InitialOrderDto initialOrderDto,
            PaymentInfoDto paymentInfoDto,
            String orderMemo,
            String trackingNumber,
            String trackingNumberRegisteredAt,
            String recoveryCompletedAt,
            String orderCancelledAt,
            String cancelReason,
            String pdfSendedAt,
            String paymentedAt,
            EPaymentMethod paymentMethod,
            EEasyPaymentProvider paymentProvider,
            String paymentReceiptUrl
    ) {
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.isAsInProgress = isAsInProgress;
        this.createdAt = createdAt;
        this.arrivedAt = arrivedAt;
        this.userDto = userDto;
        this.documentDtos = documentDtos;
        this.initialOrderDto = initialOrderDto;
        this.paymentInfoDto = paymentInfoDto;
        this.orderMemo = orderMemo;
        this.trackingNumber = trackingNumber;
        this.trackingNumberRegisteredAt = trackingNumberRegisteredAt;
        this.recoveryCompletedAt = recoveryCompletedAt;
        this.orderCancelledAt = orderCancelledAt;
        this.cancelReason = cancelReason;
        this.pdfSendedAt = pdfSendedAt;
        this.paymentedAt = paymentedAt;
        this.paymentMethod = paymentMethod;
        this.paymentProvider = paymentProvider;
        this.paymentReceiptUrl = paymentReceiptUrl;
        this.validateSelf();
    }

    public static ReadAdminOrderDetailResponseDto fromEntity(Order order,
                                                             Map<Document, List<Pdf>> documentPdfsMap) {

        return ReadAdminOrderDetailResponseDto.builder()
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .isAsInProgress(order.getIsAsInProgress())
                .createdAt(DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()))
                .arrivedAt(order.getArrivedAt() == null ? " - " :
                        DateTimeUtil.convertLocalDateTimeToDartString(order.getArrivedAt()))
                .userDto(UserDto.fromEntity(order))
                .documentDtos(order.getDocuments().stream()
                        .map(document -> DocumentDto.of(document, documentPdfsMap.get(document)))
                        .toList())
                .initialOrderDto(order.getInitialOrder() != null ?
                        InitialOrderDto.fromEntity(order) : null)
                .paymentInfoDto(PaymentInfoDto.fromEntity(order))
                .orderMemo(order.getMemo())
                .trackingNumber(
                        order.getDelivery().getTrackingNumber() != null ? order.getDelivery().getTrackingNumber()
                                : null)
                .trackingNumberRegisteredAt(order.getDelivery().getTrackingNumberRegisteredAt() != null
                        ? DateTimeUtil.convertLocalDateTimeToDartString(
                        order.getDelivery().getTrackingNumberRegisteredAt())
                        : null)
                .recoveryCompletedAt(order.getRecoveryCompletedAt() != null
                        ? DateTimeUtil.convertLocalDateTimeToDartString(order.getRecoveryCompletedAt())
                        : null)
                .orderCancelledAt(order.getCancelledAt() != null
                        ? DateTimeUtil.convertLocalDateTimeToDartString(order.getCancelledAt())
                        : null)
                .cancelReason(order.getCancelReason() != null ? order.getCancelReason() : null)
                .pdfSendedAt(order.getPdfSendDate() != null
                        ? DateTimeUtil.convertLocalDateTimeToDartString(order.getPdfSendDate())
                        : null)
                .paymentedAt(order.getPayment() != null && order.getPayment().getApprovedAt() != null
                        ? DateTimeUtil.convertLocalDateTimeToDartString(order.getPayment().getApprovedAt())
                        : null)
                .paymentMethod(order.getPayment() != null ? order.getPayment().getMethod() : null)
                .paymentProvider(order.getPayment() != null ? order.getPayment().getEasyPaymentProvider() : null)
                .paymentReceiptUrl(order.getPayment() != null ? order.getPayment().getReceiptUrl() : null)
                .build();
    }


    @Getter
    public static class UserDto extends SelfValidating<UserDto> {

        @JsonProperty("id")
        @NotNull
        private final Long id;

        @JsonProperty("name")
        @NotBlank
        private final String name;

        @JsonProperty("phone_number")
        @NotBlank
        private final String phoneNumber;

        @JsonProperty("email")
        private final String email;

        @JsonProperty("address")
        private final AddressResponseDto address;

        @JsonProperty("delivery_id")
        private final String deliveryId;

        @JsonProperty("delivery_request")
        private final String deliveryRequest;

        @Builder
        public UserDto(Long id, String name, String phoneNumber, String email, AddressResponseDto address,
                       String deliveryId, String deliveryRequest) {
            this.id = id;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
            this.deliveryId = deliveryId;
            this.deliveryRequest = deliveryRequest;
            this.validateSelf();
        }

        public static UserDto fromEntity(Order order) {
            return UserDto.builder()
                    .id(order.getDelivery().getId())
                    .name(order.getDelivery().getReceiverName())
                    .phoneNumber(order.getDelivery().getPhoneNumber())
                    .email(order.getDelivery().getEmail())
                    .address(order.getDelivery().getAddress() != null
                            ? AddressResponseDto.fromEntity(order.getDelivery().getAddress())
                            : null)
                    .deliveryId(order.getDelivery().getId().toString())
                    .deliveryRequest(order.getDelivery().getRequest() != null
                            ? order.getDelivery().getRequest() : null)
                    .build();
        }
    }

    @Getter
    public static class DocumentDto extends SelfValidating<DocumentDto> {

        @JsonProperty("id")
        @NotNull
        private final String id;

        @JsonProperty("name")
        @NotBlank
        private final String name;

        @JsonProperty("page_count")
        @NotNull
        private final Integer pageCount;

        @JsonProperty("page_price")
        @NotNull
        private final Integer pagePrice;

        @JsonProperty("recovery_option")
        @NotNull
        private final ERecoveryOption recoveryOption;

        @JsonProperty("recovery_option_price")
        @NotNull
        private final Integer recoveryOptionPrice;

        @JsonProperty("is_ocr_enabled")
        @NotNull
        private final Boolean isOcrEnabled;

        @JsonProperty("ocr_price")
        @NotNull
        private final Integer ocrPrice;

        @JsonProperty("total_price")
        @NotNull
        private final Integer totalPrice;

        @JsonProperty("pdfs")
        private final List<PdfDto> pdfs;

        @Builder
        public DocumentDto(String id, String name, Integer pageCount, Integer pagePrice, ERecoveryOption recoveryOption,
                           Integer recoveryOptionPrice, Boolean isOcrEnabled, Integer ocrPrice, Integer totalPrice,
                           List<PdfDto> pdfs
        ) {
            this.id = id;
            this.name = name;
            this.pageCount = pageCount;
            this.pagePrice = pagePrice;
            this.recoveryOption = recoveryOption;
            this.recoveryOptionPrice = recoveryOptionPrice;
            this.isOcrEnabled = isOcrEnabled;
            this.ocrPrice = ocrPrice;
            this.totalPrice = totalPrice;
            this.pdfs = pdfs;
            this.validateSelf();
        }

        public static DocumentDto of(Document document, List<Pdf> pdfs) {
            return DocumentDto.builder()
                    .id(document.getId().toString())
                    .name(document.getName())
                    .pageCount(document.getPageCount())
                    .pagePrice(document.getPagePrice())
                    .recoveryOption(document.getRecoveryOption())
                    .recoveryOptionPrice(document.getRecoveryOptionPrice())
                    .isOcrEnabled(document.getIsOcrEnabled())
                    .ocrPrice(document.getOcrPrice())
                    .totalPrice(document.getDocumentPrice())
                    .pdfs(pdfs.isEmpty() ? List.of() :
                            pdfs.stream()
                                    .map(PdfDto::fromEntity)
                                    .toList())
                    .build();
        }

        @Getter
        public static class PdfDto extends SelfValidating<PdfDto> {

            @JsonProperty("id")
            @NotBlank
            private final String id;

            @JsonProperty("pdf_url")
            @NotBlank
            private final String pdfUrl;

            @JsonProperty("is_expired")
            private final Boolean isExpired;

            @JsonProperty("expired_at")
            private final String expiredAt;

            @Builder
            public PdfDto(String pdfUrl, Boolean isExpired, String expiredAt, String id) {
                this.pdfUrl = pdfUrl;
                this.isExpired = isExpired;
                this.expiredAt = expiredAt;
                this.id = id;
                this.validateSelf();
            }

            public static PdfDto fromEntity(Pdf pdf) {
                return PdfDto.builder()
                        .pdfUrl(pdf.getPdfUrl())
                        .isExpired(pdf.getExpiredAt() != null)
                        .expiredAt(pdf.getExpiredAt() != null
                                ? DateTimeUtil.convertLocalDateTimeToDartString(pdf.getExpiredAt()) : null)
                        .id(pdf.getId().toString())
                        .build();
            }
        }
    }

    @Getter
    public static class InitialOrderDto extends SelfValidating<InitialOrderDto> {

        @JsonProperty("order_number")
        @NotBlank
        private final String orderNumber;

        @JsonProperty("created_at")
        @NotBlank
        private final String createdAt;

        @JsonProperty("initial_documents")
        private final List<InitialDocumentDto> initialDocumentDtos;

        @JsonProperty("initial_payment_info")
        @NotNull
        private final InitialPaymentInfoDto initialPaymentInfoDto;

        @Builder
        public InitialOrderDto(String orderNumber, String createdAt,
                               List<InitialDocumentDto> initialDocumentDtos,
                               InitialPaymentInfoDto initialPaymentInfoDto) {
            this.orderNumber = orderNumber;
            this.createdAt = createdAt;
            this.initialDocumentDtos = initialDocumentDtos;
            this.initialPaymentInfoDto = initialPaymentInfoDto;
            this.validateSelf();
        }

        public static InitialOrderDto fromEntity(Order order) {
            return InitialOrderDto.builder()
                    .orderNumber(order.getOrderNumber())
                    .createdAt(DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()))
                    .initialDocumentDtos(order.getInitialOrder().getInitialDocuments().stream()
                            .map(InitialDocumentDto::of)
                            .toList())
                    .initialPaymentInfoDto(InitialPaymentInfoDto.fromEntity(order.getInitialOrder()))
                    .build();
        }

        @Getter
        public static class InitialDocumentDto extends SelfValidating<InitialDocumentDto> {

            @JsonProperty("name")
            @NotBlank
            private final String name;

            @JsonProperty("page_count")
            @NotNull
            private final Integer pageCount;

            @JsonProperty("page_price")
            @NotNull
            private final Integer pagePrice;

            @JsonProperty("recovery_option")
            @NotNull
            private final ERecoveryOption recoveryOption;

            @JsonProperty("recovery_option_price")
            @NotNull
            private final Integer recoveryOptionPrice;

            @JsonProperty("is_ocr_enabled")
            @NotNull
            private final Boolean isOcrEnabled;

            @JsonProperty("ocr_price")
            @NotNull
            private final Integer ocrPrice;

            @JsonProperty("total_price")
            @NotNull
            private final Integer totalPrice;

            @Builder
            public InitialDocumentDto(String name, Integer pageCount, Integer pagePrice,
                                      ERecoveryOption recoveryOption,
                                      Integer recoveryOptionPrice, Boolean isOcrEnabled, Integer ocrPrice,
                                      Integer totalPrice
            ) {
                this.name = name;
                this.pageCount = pageCount;
                this.pagePrice = pagePrice;
                this.recoveryOption = recoveryOption;
                this.recoveryOptionPrice = recoveryOptionPrice;
                this.isOcrEnabled = isOcrEnabled;
                this.ocrPrice = ocrPrice;
                this.totalPrice = totalPrice;
                this.validateSelf();
            }

            public static InitialDocumentDto of(InitialDocument document) {
                return InitialDocumentDto.builder()
                        .name(document.getName())
                        .pageCount(document.getPageCount())
                        .pagePrice(document.getPagePrice())
                        .recoveryOption(document.getRecoveryOption())
                        .recoveryOptionPrice(document.getRecoveryOptionPrice())
                        .isOcrEnabled(document.getIsOcrEnabled())
                        .ocrPrice(document.getOcrPrice())
                        .totalPrice(document.getDocumentsPrice())
                        .build();
            }
        }

        @Getter
        public static class InitialPaymentInfoDto extends SelfValidating<InitialPaymentInfoDto> {

            @JsonProperty("documents_price")
            @NotNull
            private final Integer documentsPrice;

            @JsonProperty("cutting_price")
            @NotNull
            private final Integer cuttingPrice;

            @JsonProperty("is_one_day_scan")
            @NotNull
            private final Boolean isOneDayScan;

            @JsonProperty("one_day_scan_price")
            @NotNull
            private final Integer oneDayScanPrice;

            @JsonProperty("delivery_price")
            @NotNull
            private final Integer deliveryPrice;

            @JsonProperty("coupon_name")
            private final String couponName;

            @JsonProperty("coupon_discount")
            @NotNull
            private final Integer couponDiscount;

            @JsonProperty("total_price")
            @NotNull
            private final Integer totalPrice;

            @Builder
            public InitialPaymentInfoDto(Integer documentsPrice, Integer cuttingPrice, Boolean isOneDayScan,
                                         Integer oneDayScanPrice, Integer deliveryPrice, Integer couponDiscount,
                                         Integer totalPrice, String couponName) {
                this.documentsPrice = documentsPrice;
                this.cuttingPrice = cuttingPrice;
                this.isOneDayScan = isOneDayScan;
                this.oneDayScanPrice = oneDayScanPrice;
                this.deliveryPrice = deliveryPrice;
                this.couponDiscount = couponDiscount;
                this.totalPrice = totalPrice;
                this.couponName = couponName;
                this.validateSelf();
            }

            public static InitialPaymentInfoDto fromEntity(InitialOrder order) {
                return InitialPaymentInfoDto.builder()
                        .documentsPrice(order.getDocumentsPrice())
                        .cuttingPrice(
                                order.getInitialDocuments().stream()
                                        .map(InitialDocument::getCuttingPrice)
                                        .reduce(0, Integer::sum)
                        )
                        .isOneDayScan(order.getIsOneDayScan())
                        .oneDayScanPrice(order.getInitialDocuments().stream()
                                .map(InitialDocument::getOneDayScanPrice)
                                .reduce(0, Integer::sum))
                        .deliveryPrice(order.getDeliveryPrice())
                        .couponName(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getName() : null)
                        .couponDiscount(order.getUsedCoupon() != null ?
                                order.getUsedCoupon().getIssuedCoupon().getDiscountPrice(order.getDocumentsTotalAmount()) : 0)
                        .totalPrice(order.getTotalAmount())
                        .build();
            }
        }


        @Getter
        public static class PaymentInfoDto extends SelfValidating<PaymentInfoDto> {

            @JsonProperty("documents_price")
            @NotNull
            private final Integer documentsPrice;

            @JsonProperty("cutting_price")
            @NotNull
            private final Integer cuttingPrice;

            @JsonProperty("is_one_day_scan")
            @NotNull
            private final Boolean isOneDayScan;

            @JsonProperty("one_day_scan_price")
            @NotNull
            private final Integer oneDayScanPrice;

            @JsonProperty("delivery_price")
            @NotNull
            private final Integer deliveryPrice;

            @JsonProperty("coupon_name")
            private final String couponName;

            @JsonProperty("coupon_discount")
            @NotNull
            private final Integer couponDiscount;

            @JsonProperty("total_price")
            @NotNull
            private final Integer totalPrice;

            @Builder
            public PaymentInfoDto(Integer documentsPrice, Integer cuttingPrice, Boolean isOneDayScan,
                                  Integer oneDayScanPrice, Integer deliveryPrice, Integer couponDiscount,
                                  Integer totalPrice, String couponName) {
                this.documentsPrice = documentsPrice;
                this.cuttingPrice = cuttingPrice;
                this.isOneDayScan = isOneDayScan;
                this.oneDayScanPrice = oneDayScanPrice;
                this.deliveryPrice = deliveryPrice;
                this.couponDiscount = couponDiscount;
                this.couponName = couponName;
                this.totalPrice = totalPrice;
                this.validateSelf();
            }

            public static PaymentInfoDto fromEntity(Order order) {
                return PaymentInfoDto.builder()
                        .documentsPrice(order.getDocumentsPrice())
                        .cuttingPrice(
                                order.getDocuments().stream()
                                        .map(Document::getCuttingPrice)
                                        .reduce(0, Integer::sum)
                        )
                        .isOneDayScan(order.getIsOneDayScan())
                        .oneDayScanPrice(order.getDocuments().stream()
                                .map(Document::getOneDayScanPrice)
                                .reduce(0, Integer::sum))
                        .deliveryPrice(order.getDelivery().getDeliveryPrice())
                        .couponName(order.getUsedCoupon() != null ? order.getUsedCoupon().getIssuedCoupon().getCouponTemplate().getName() : null)
                        .couponDiscount(order.getUsedCoupon() != null ?
                                order.getUsedCoupon().getIssuedCoupon().getDiscountPrice(order.getDocumentsTotalAmount()) : 0)
                        .totalPrice(order.getTotalAmount())
                        .build();
            }
        }
    }
}

