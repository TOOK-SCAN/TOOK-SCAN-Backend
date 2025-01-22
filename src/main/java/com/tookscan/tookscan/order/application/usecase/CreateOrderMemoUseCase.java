package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.application.dto.request.CreateOrderMemoRequestDto;

@UseCase
public interface CreateOrderMemoUseCase {
    void execute(Long orderId, CreateOrderMemoRequestDto requestDto);
}
