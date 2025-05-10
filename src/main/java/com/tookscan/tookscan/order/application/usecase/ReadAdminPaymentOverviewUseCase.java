package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminPaymentOverviewResponseDto;

@UseCase
public interface ReadAdminPaymentOverviewUseCase {
    ReadAdminPaymentOverviewResponseDto execute(Long orderId);
}
