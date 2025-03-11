package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminDeliveriesOverviewsResponseDto;

@UseCase
public interface ReadAdminDeliveriesOverviewsUseCase {
    ReadAdminDeliveriesOverviewsResponseDto execute(Integer page, Integer size, String startDate, String endDate,
                                                    String search, String searchType);
}
