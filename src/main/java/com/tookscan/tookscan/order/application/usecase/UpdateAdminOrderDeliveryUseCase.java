package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryRequestDto;

@UseCase
public interface UpdateAdminOrderDeliveryUseCase {
    void execute(Long deliveryId,UpdateAdminOrderDeliveryRequestDto requestDto);
}
