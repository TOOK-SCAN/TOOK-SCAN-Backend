package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadGuestOrderDetailResponseDto;

@UseCase
public interface ReadGuestOrderDetailUseCase {
    ReadGuestOrderDetailResponseDto execute(String orderNumber);
}
