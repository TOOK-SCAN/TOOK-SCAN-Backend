package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderSummaryResponseDto;

@UseCase
public interface ReadGuestOrderSummaryUseCase {
    ReadGuestOrderSummaryResponseDto execute(String orderNumber);
}
