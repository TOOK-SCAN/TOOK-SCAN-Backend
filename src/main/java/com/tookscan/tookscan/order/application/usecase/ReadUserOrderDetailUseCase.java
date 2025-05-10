package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderDetailResponseDto;
import java.util.UUID;

@UseCase
public interface ReadUserOrderDetailUseCase {
    ReadUserOrderDetailResponseDto execute(UUID accountID, Long orderId);
}
