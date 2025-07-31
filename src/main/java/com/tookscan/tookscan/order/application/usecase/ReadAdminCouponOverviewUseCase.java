package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminCouponOverviewResponseDto;

public interface ReadAdminCouponOverviewUseCase {

    ReadAdminCouponOverviewResponseDto execute(
            ECouponFormat format,
            ECouponType type,
            String status,
            Integer page,
            Integer size
    );
}
