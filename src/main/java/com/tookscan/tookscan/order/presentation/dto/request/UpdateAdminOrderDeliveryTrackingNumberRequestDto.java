package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateAdminOrderDeliveryTrackingNumberRequestDto(
        @JsonProperty("tracking_number")
        @NotNull(message = "운송장 번호를 입력해주세요.")
        @Pattern(regexp = "^[0-9]{9,}$", message = "운송장 번호는 숫자 9자리 이상이어야 합니다.")
        String trackingNumber
) {
}
