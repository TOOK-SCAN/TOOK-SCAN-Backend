package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderDeliveryResponseDto;

@UseCase
public interface ReadGuestOrderDeliveryUseCase {
    ReadGuestOrderDeliveryResponseDto execute(Long orderId);
}
