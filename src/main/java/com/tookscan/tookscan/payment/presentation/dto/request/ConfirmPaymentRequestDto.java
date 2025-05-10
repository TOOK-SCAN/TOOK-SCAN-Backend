package com.tookscan.tookscan.payment.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmPaymentRequestDto(
        @JsonProperty("order_number")
        @NotBlank(message = "주문번호는 필수입니다.")
        String orderNumber,
        @JsonProperty("payment_key")
        @NotBlank(message = "결제키는 필수입니다.")
        String paymentKey,
        @JsonProperty("amount")
        @NotNull(message = "결제금액은 필수입니다.")
        Integer amount
) {
}
