package com.tookscan.tookscan.payment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ConfirmPaymentResponseDto {
    @JsonProperty("payment_result")
    private final boolean paymentResult;

    @JsonProperty("order_number")
    private final String orderNumber;

    @JsonProperty("approved_at")
    private final String approvedAt;

    @JsonProperty("method")
    private final EPaymentMethod method;

    @JsonProperty("easy_payment_provider")
    private final EEasyPaymentProvider easyPaymentProvider;

    @JsonProperty("total_amount")
    private final Integer totalAmount;

    @JsonProperty("fail_reason")
    private final String failReason;

    @JsonProperty("customer_key")
    private final UUID customerKey;

    @Builder
    public ConfirmPaymentResponseDto(boolean paymentResult, String orderNumber, String approvedAt, EPaymentMethod method, EEasyPaymentProvider easyPaymentProvider, Integer totalAmount, String failReason, UUID customerKey) {
        this.paymentResult = paymentResult;
        this.orderNumber = orderNumber;
        this.approvedAt = approvedAt;
        this.method = method;
        this.easyPaymentProvider = easyPaymentProvider;
        this.totalAmount = totalAmount;
        this.failReason = failReason;
        this.customerKey = customerKey;
    }

    public static ConfirmPaymentResponseDto of(boolean paymentResult, String orderNumber, String approvedAt, EPaymentMethod method, EEasyPaymentProvider easyPaymentProvider, Integer totalAmount, String failReason, UUID customerKey) {
        return ConfirmPaymentResponseDto.builder()
                .paymentResult(paymentResult)
                .orderNumber(orderNumber)
                .approvedAt(approvedAt)
                .method(method)
                .easyPaymentProvider(easyPaymentProvider)
                .totalAmount(totalAmount)
                .failReason(failReason)
                .customerKey(customerKey)
                .build();
    }
}
