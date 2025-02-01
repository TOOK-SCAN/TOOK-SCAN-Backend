package com.tookscan.tookscan.order.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DeleteAdminOrdersRequestDto(
        @JsonProperty("orderIds")
        @NotNull
        @NotEmpty
        List<Long> orderIds
) {
}
