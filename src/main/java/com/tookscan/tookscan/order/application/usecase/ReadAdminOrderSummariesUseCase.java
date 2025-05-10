package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderSummariesResponseDto;
import org.springframework.data.domain.Sort.Direction;

@UseCase
public interface ReadAdminOrderSummariesUseCase {
    ReadAdminOrderSummariesResponseDto execute(Integer page, Integer size, String startDate, String endDate,
                                               String search, String searchType, String sort, Direction direction);
}
