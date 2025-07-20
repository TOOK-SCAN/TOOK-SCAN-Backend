package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ExportAdminDeliveriesRequestDto(
        @JsonProperty("order_ids")
        @NotEmpty(message = "주문 ID 목록을 입력해주세요.")
        List<Long> orderIds
) {
}