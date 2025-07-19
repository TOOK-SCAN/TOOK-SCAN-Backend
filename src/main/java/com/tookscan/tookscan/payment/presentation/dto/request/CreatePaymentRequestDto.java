package com.tookscan.tookscan.payment.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;

import java.time.LocalDateTime;

public record CreatePaymentRequestDto(
        @JsonProperty("order_id")
        Long orderId,

        @JsonProperty("total_amount")
        Integer totalAmount,

        @JsonProperty("approved_at")
        LocalDateTime approvedAt,

        @JsonProperty("method")
        EPaymentMethod method
) {
}
