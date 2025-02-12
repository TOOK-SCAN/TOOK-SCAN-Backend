package com.tookscan.tookscan.order.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Valid
public record UpdateAdminOrdersStatusRequestDto (
        @JsonProperty("order_ids")
        @NotEmpty(message = "주문 번호를 선택해주세요.")
        List<Long> orderIds,

        @JsonProperty("status")
        @NotNull(message = "주문 상태를 입력해주세요.")
        EOrderStatus status
){ }
