package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.CreateUserOrderRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.CreateUserOrderResponseDto;
import java.util.UUID;

@UseCase
public interface CreateUserOrderUseCase {
    CreateUserOrderResponseDto execute(UUID accountId, CreateUserOrderRequestDto requestDto);
}
