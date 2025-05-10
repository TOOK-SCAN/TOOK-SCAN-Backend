package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.CreateAdminOrderCouponRequestDto;

@UseCase
public interface CreateAdminOrderCouponUseCase {
    void execute(CreateAdminOrderCouponRequestDto requestDto);
}
