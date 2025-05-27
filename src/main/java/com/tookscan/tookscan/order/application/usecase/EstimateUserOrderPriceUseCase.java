package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.EstimateUserOrderPriceRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.EstimateUserOrderPriceResponseDto;

@UseCase
public interface EstimateUserOrderPriceUseCase {
    EstimateUserOrderPriceResponseDto execute(EstimateUserOrderPriceRequestDto requestDto);
}
