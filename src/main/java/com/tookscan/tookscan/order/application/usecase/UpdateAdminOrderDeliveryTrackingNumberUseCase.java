package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryTrackingNumberRequestDto;

@UseCase
public interface UpdateAdminOrderDeliveryTrackingNumberUseCase {
    void execute(Long deliveryId, UpdateAdminOrderDeliveryTrackingNumberRequestDto requestDto);
}
