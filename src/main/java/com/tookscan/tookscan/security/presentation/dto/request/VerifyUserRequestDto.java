package com.tookscan.tookscan.security.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VerifyUserRequestDto(
        @JsonProperty("serial_id")
        String serialId,
        @JsonProperty("phone_number")
        String phoneNumber,
        @JsonProperty("name")
        String name
) {
}
