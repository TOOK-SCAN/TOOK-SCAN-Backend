package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateAdminOrderDeliveryTrackingNumberRequestDto(
        @JsonProperty("tracking_number")
        @NotNull(message = "운송장 번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{4}-\\d{4}-\\d{4}$",
                message = "운송장 번호는 1234-5678-9101 형식의 12자리 숫자여야 합니다."
        )
        String trackingNumber
) {
}
