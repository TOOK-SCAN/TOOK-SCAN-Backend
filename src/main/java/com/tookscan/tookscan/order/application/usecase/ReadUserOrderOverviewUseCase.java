package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderOverviewResponseDto;
import java.util.UUID;

@UseCase
public interface ReadUserOrderOverviewUseCase {
    ReadUserOrderOverviewResponseDto execute(UUID accountId, Integer page, Integer size, String sort, String search,
                                             String direction,
                                             String startDate, String endDate, EOrderStatus orderStatus);
}
