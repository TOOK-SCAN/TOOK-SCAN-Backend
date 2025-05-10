package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderCouponDetailResponseDto;
import java.util.UUID;

@UseCase
public interface ReadUserOrderCouponDetailUseCase {
    ReadUserOrderCouponDetailResponseDto execute(UUID accountId, String couponCode);
}
