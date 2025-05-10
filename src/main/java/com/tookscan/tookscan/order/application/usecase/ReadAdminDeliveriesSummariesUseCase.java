package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminDeliveriesSummariesResponseDto;

@UseCase
public interface ReadAdminDeliveriesSummariesUseCase {
    ReadAdminDeliveriesSummariesResponseDto execute(Integer page, Integer size, String startDate, String endDate,
                                                    String search, String searchType);
}
