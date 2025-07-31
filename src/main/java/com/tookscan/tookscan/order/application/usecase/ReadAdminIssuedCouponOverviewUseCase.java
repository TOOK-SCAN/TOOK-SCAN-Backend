package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminIssuedCouponOverviewResponseDto;

public interface ReadAdminIssuedCouponOverviewUseCase {

    ReadAdminIssuedCouponOverviewResponseDto execute(Long id, Integer page, Integer size);
}
