package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminUsedCouponOverviewResponseDto;

@UseCase
public interface ReadAdminUsedCouponOverviewUseCase {

    ReadAdminUsedCouponOverviewResponseDto execute(Long id, String search, String searchType, Integer page, Integer size);
}
