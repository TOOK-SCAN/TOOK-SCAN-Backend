package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderOverviewsResponseDto;
import org.springframework.data.domain.Sort.Direction;

@UseCase
public interface ReadAdminOrderOverviewsUseCase {
    ReadAdminOrderOverviewsResponseDto execute(int page, int size, String startDate, String endDate, String search,
                                               String searchType, String sort, Direction direction,
                                               EOrderStatus orderStatus, Boolean isOneDayScan,
                                               Boolean hasRecoveryOption,
                                               Boolean isAsInProgress, Boolean isInProgress);
}
