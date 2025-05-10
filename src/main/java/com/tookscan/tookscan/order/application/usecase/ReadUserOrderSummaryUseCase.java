package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderSummaryResponseDto;
import java.util.UUID;

@UseCase
public interface ReadUserOrderSummaryUseCase {
    ReadUserOrderSummaryResponseDto execute(UUID accountId, String orderNumber);
}
