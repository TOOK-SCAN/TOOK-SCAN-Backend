package com.tookscan.tookscan.order.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateAdminOrdersStatusRecoveryOptionRequestDto(
        @NotNull @NotEmpty List<Long> orderIds
) {}