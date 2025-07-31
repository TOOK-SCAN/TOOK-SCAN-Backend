package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminCouponTemplateOverviewResponseDto;

public interface ReadAdminCouponTemplateOverviewUseCase {

    ReadAdminCouponTemplateOverviewResponseDto execute(
            ECouponFormat format,
            ECouponType type,
            String status,
            Integer page,
            Integer size
    );
}
