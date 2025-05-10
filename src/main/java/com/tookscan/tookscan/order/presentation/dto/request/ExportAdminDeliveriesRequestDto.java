package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ExportAdminDeliveriesRequestDto(
        @JsonProperty("start_date")
        @NotBlank(message = "시작일을 입력해주세요.")
        @Pattern(regexp = "\\d{4}\\.\\d{2}\\.\\d{2}")
        String startDate,

        @JsonProperty("end_date")
        @NotBlank(message = "종료일을 입력해주세요.")
        @Pattern(regexp = "\\d{4}\\.\\d{2}\\.\\d{2}")
        String endDate
) {
}