package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderRequestDto;

@UseCase
public interface UpdateAdminOrderUseCase {
    void execute(Long orderId, UpdateAdminOrderRequestDto requestDto);
}
