package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

public record UpdateUserOrderInfoRequestDto(
        @JsonProperty("email")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @JsonProperty("address")
        @Valid
        AddressRequestDto address,

        @JsonProperty("delivery_request")
        String deliveryRequest
) {
}
