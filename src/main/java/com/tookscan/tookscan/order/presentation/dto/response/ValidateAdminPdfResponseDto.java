package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ValidateAdminPdfResponseDto extends SelfValidating<ValidateAdminPdfResponseDto> {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("phone_number")
    private final String phoneNumber;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("order_number")
    private final String orderNumber;

    public ValidateAdminPdfResponseDto(String name, String phoneNumber, String createdAt, String orderNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.orderNumber = orderNumber;

        this.validateSelf();
    }

    public static ValidateAdminPdfResponseDto of(String name, String phoneNumber, String createdAt, String orderNumber) {
        return new ValidateAdminPdfResponseDto(name, phoneNumber, createdAt, orderNumber);
    }
}
