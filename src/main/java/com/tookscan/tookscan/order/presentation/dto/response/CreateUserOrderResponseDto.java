package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateUserOrderResponseDto extends SelfValidating<CreateUserOrderResponseDto> {

    @JsonProperty("order_id")
    @NotNull
    private String orderId;

    @JsonProperty("order_number")
    @NotNull
    private String orderNumber;

    @Builder
    public CreateUserOrderResponseDto(String orderNumber, String orderId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.validateSelf();
    }
}
