package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ReadAdminPaymentOverviewResponseDto extends SelfValidating<ReadAdminPaymentOverviewResponseDto> {
    @JsonProperty("documents_price")
    @NotNull(message = "문서 가격은 필수입니다.")
    private final Integer documentsPrice;

    @JsonProperty("delivery_price")
    @NotNull(message = "배송비는 필수입니다.")
    private final Integer deliveryPrice;

    @JsonProperty("refund_price")
    private final Integer refundPrice;

    @JsonProperty("payment_method")
    @NotNull(message = "결제 수단은 필수입니다.")
    private final EPaymentMethod paymentMethod;

    @JsonProperty("payment_easy_pay")
    private final EEasyPaymentProvider paymentEasyPay;

    @JsonProperty("total_price")
    @NotNull(message = "총 가격은 필수입니다.")
    private final Integer totalPrice;

    @Builder
    public ReadAdminPaymentOverviewResponseDto(Integer documentsPrice, Integer deliveryPrice, Integer refundPrice, EPaymentMethod paymentMethod, EEasyPaymentProvider paymentEasyPay, Integer totalPrice) {
        this.documentsPrice = documentsPrice;
        this.deliveryPrice = deliveryPrice;
        this.refundPrice = refundPrice;
        this.paymentMethod = paymentMethod;
        this.paymentEasyPay = paymentEasyPay;
        this.totalPrice = totalPrice;
        this.validateSelf();
    }

    public static ReadAdminPaymentOverviewResponseDto of(Order order) {

        if (order.getPayment() == null) {
            return null;
        }

        return ReadAdminPaymentOverviewResponseDto.builder()
                .documentsPrice(order.getDocumentsTotalAmount())
                .deliveryPrice(order.getDelivery().getDeliveryPrice())
//                .refundPrice(order.getPayment().getStatus()) //TODO: 환불 경우 계산
                .paymentMethod(order.getPayment().getMethod())
                .paymentEasyPay(order.getPayment().getEasyPaymentProvider())
                .totalPrice(order.getTotalAmount())
                .build();
    }
}
