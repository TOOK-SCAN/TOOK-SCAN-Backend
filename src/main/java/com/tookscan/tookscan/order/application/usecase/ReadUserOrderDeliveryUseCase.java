package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderDeliveryResponseDto;
import java.util.UUID;

@UseCase
public interface ReadUserOrderDeliveryUseCase {
    ReadUserOrderDeliveryResponseDto execute(UUID accountId, Long orderId);
}
