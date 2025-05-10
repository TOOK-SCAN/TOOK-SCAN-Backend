package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateAdminOrderDeliveryRequestDto(

        @JsonProperty("receiver_name")
        @NotBlank(message = "받는 이를 입력해주세요.")
        String receiverName,

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "전화번호 형식이 올바르지 않습니다. (- 없이 입력해주세요, 예: 01012345678)"
        )
        String phoneNumber,

        @JsonProperty("address")
        @NotNull(message = "주소를 입력해주세요.")
        AddressRequestDto address,

        @JsonProperty("request")
        String request,

        @JsonProperty("tracking_number")
        String trackingNumber,

        @JsonProperty("email")
        String email
) {
}
