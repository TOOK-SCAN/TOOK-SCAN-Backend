package com.tookscan.tookscan.order.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DeleteAdminOrdersRequestDto(
        @JsonProperty("orderIds")
        @NotEmpty(message = "주문을 선택해주세요.")
        List<Long> orderIds
) {
}
