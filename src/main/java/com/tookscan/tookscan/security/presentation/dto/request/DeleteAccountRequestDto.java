package com.tookscan.tookscan.security.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteAccountRequestDto(
        @JsonProperty("reason")
        String reason
) {
}
