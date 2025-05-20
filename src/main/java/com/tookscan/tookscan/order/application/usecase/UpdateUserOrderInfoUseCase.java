package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderInfoRequestDto;
import java.util.UUID;

@UseCase
public interface UpdateUserOrderInfoUseCase {
    void execute(UUID accountId, Long orderId, UpdateUserOrderInfoRequestDto requestDto);
}
