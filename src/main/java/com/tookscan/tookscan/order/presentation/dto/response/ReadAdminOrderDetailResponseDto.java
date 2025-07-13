package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ReadAdminOrderDetailResponseDto extends
        SelfValidating<ReadAdminOrderDetailResponseDto> {

    @JsonProperty("order_number")
    @NotBlank
    private final String orderNumber;

    @JsonProperty("order_status")
    @NotNull
    private final EOrderStatus orderStatus;

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

    @JsonProperty("payment_info")
    @NotNull
    private final PaymentInfoDto paymentInfoDto;

    @JsonProperty("order_memo")
    private final String orderMemo;

    @Builder
    public ReadAdminOrderDetailResponseDto(
            String orderNumber,
            EOrderStatus orderStatus,
            String createdAt,
            String arrivedAt,
            UserDto userDto,
            List<DocumentDto> documentDtos,
            PaymentInfoDto paymentInfoDto,
            String orderMemo
    ) {
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.arrivedAt = arrivedAt;
        this.userDto = userDto;
        this.documentDtos = documentDtos;
        this.paymentInfoDto = paymentInfoDto;
        this.orderMemo = orderMemo;
        this.validateSelf();
    }

    public static ReadAdminOrderDetailResponseDto fromEntity(Order order,
                                                            Map<Document, List<Pdf>> documentPdfsMap) {

        boolean isAdminChecked = order.getOrderStatus().getCode() >= EOrderStatus.APPLY_COMPLETED.getCode();
        return ReadAdminOrderDetailResponseDto.builder()
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .createdAt(DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()))
                .arrivedAt(order.getArrivedAt() == null ? " - " :
                        DateTimeUtil.convertLocalDateTimeToDartString(order.getArrivedAt()))
                .userDto(UserDto.fromEntity(order))
                .documentDtos(order.getDocuments().stream()
                        .map(document -> DocumentDto.of(document, isAdminChecked, documentPdfsMap.get(document)))
                        .toList())
                .paymentInfoDto(PaymentInfoDto.fromEntity(order))
                .orderMemo(order.getMemo())
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
        private final String address;

        @Builder
        public UserDto(Long id, String name, String phoneNumber, String email, String address) {
            this.id = id;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
            this.validateSelf();
        }

        public static UserDto fromEntity(Order order) {
            return UserDto.builder()
                    .id(order.getDelivery().getId())
                    .name(order.getDelivery().getReceiverName())
                    .phoneNumber(order.getDelivery().getPhoneNumber())
                    .email(order.getDelivery().getEmail())
                    .address(order.getDelivery().getAddress().getFullAddressWithZoneCode() + " / " + order.getDelivery().getRequest())
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
        private final List<String> pdfs;

        @Builder
        public DocumentDto(String id, String name, Integer pageCount, Integer pagePrice, ERecoveryOption recoveryOption,
                           Integer recoveryOptionPrice, Boolean isOcrEnabled, Integer ocrPrice, Integer totalPrice, List<String> pdfs
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

        public static DocumentDto of(Document document, Boolean isAdminChecked,
                                     List<Pdf> pdfs) {

            // 관리자 검수 이후면 문서의 최종 정보로 반환
            if (isAdminChecked) {
                return DocumentDto.builder()
                        .id(document.getId().toString())
                        .name(document.getName())
                        .pageCount(document.getPageCount())
                        .pagePrice(document.calculateDocumentPrice())
                        .recoveryOption(document.getRecoveryOption())
                        .recoveryOptionPrice(document.calculateRecoveryOptionPrice())
                        .isOcrEnabled(document.getIsOcrEnabled())
                        .ocrPrice(document.calculateOcrPrice())
                        .totalPrice(document.calculatePrice())
                        .pdfs(pdfs.isEmpty() ? List.of() :
                                pdfs.stream()
                                        .map(Pdf::getPdfUrl)
                                        .toList())
                        .build();
            }

            // 관리자 검수 이전이면 문서의 초기 정보로 반환
            return DocumentDto.builder()
                    .id(document.getId().toString())
                    .name(document.getInitialName())
                    .pageCount(document.getInitialPageCount())
                    .pagePrice(document.calculateInitialDocumentPrice())
                    .recoveryOption(document.getInitialRecoveryOption())
                    .recoveryOptionPrice(document.calculateInitialRecoveryOptionPrice())
                    .isOcrEnabled(document.getInitialIsOcrEnabled())
                    .ocrPrice(document.calculateInitialOcrPrice())
                    .totalPrice(document.calculateInitialPrice())
                    .pdfs(document.getPdfs().isEmpty() ? List.of() :
                            document.getPdfs().stream()
                                    .map(Pdf::getPdfUrl)
                                    .toList())
                    .build();
        }
    }

    @Getter
    public static class PaymentInfoDto extends SelfValidating<PaymentInfoDto> {

        @JsonProperty("documents_price")
        @NotNull
        private final Integer documentsPrice;

        @JsonProperty("default_price")
        @NotNull
        private final Integer defaultPrice;

        @JsonProperty("is_one_day_scan")
        @NotNull
        private final Boolean isOneDayScan;

        @JsonProperty("one_day_scan_price")
        @NotNull
        private final Integer oneDayScanPrice;

        @JsonProperty("delivery_price")
        @NotNull
        private final Integer deliveryPrice;

        @JsonProperty("coupon_discount")
        @NotNull
        private final Integer couponDiscount;

        @JsonProperty("total_price")
        @NotNull
        private final Integer totalPrice;

        @Builder
        public PaymentInfoDto(Integer documentsPrice, Integer defaultPrice, Boolean isOneDayScan,
                              Integer oneDayScanPrice, Integer deliveryPrice, Integer couponDiscount,
                              Integer totalPrice) {
            this.documentsPrice = documentsPrice;
            this.defaultPrice = defaultPrice;
            this.isOneDayScan = isOneDayScan;
            this.oneDayScanPrice = oneDayScanPrice;
            this.deliveryPrice = deliveryPrice;
            this.couponDiscount = couponDiscount;
            this.totalPrice = totalPrice;
            this.validateSelf();
        }

        public static PaymentInfoDto fromEntity(Order order) {
            return PaymentInfoDto.builder()
                    .documentsPrice(order.getDocumentsTotalAmount())
                    .defaultPrice(
                            order.getDocuments().stream()
                                    .map(Document::getPricePolicy)
                                    .map(PricePolicy::getDefaultPrice)
                                    .reduce(0, Integer::sum)
                    )
                    .isOneDayScan(order.getIsOneDayScan())
                    .oneDayScanPrice(order.getDocuments().stream()
                            .map(Document::calculateOneDayScanPrice)
                            .reduce(0, Integer::sum))
                    .deliveryPrice(order.getDelivery().getDeliveryPrice())
                    .couponDiscount(0) // TODO: 쿠폰 할인 로직 추가 필요
                    .totalPrice(order.getTotalAmount())
                    .build();
        }
    }
}

