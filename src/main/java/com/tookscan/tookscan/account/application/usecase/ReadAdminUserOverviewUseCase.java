package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserOverviewResponseDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface ReadAdminUserOverviewUseCase {
    /**
     * 관리자 유저 리스트 조회 유스케이스
     */
    ReadAdminUserOverviewResponseDto execute(
            String searchType,
            String search,
            Long groupId,
            String startDate,
            String endDate,
            Integer page,
            Integer size
    );
}
