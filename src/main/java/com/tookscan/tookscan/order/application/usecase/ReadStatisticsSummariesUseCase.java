package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadStatisticsSummariesResponseDto;

@UseCase
public interface ReadStatisticsSummariesUseCase {
    ReadStatisticsSummariesResponseDto execute(
            String startYearMonth,
            String endYearMonth,
            Boolean isApplied,
            Boolean isArrived,
            Boolean isCompleted
    );
}
