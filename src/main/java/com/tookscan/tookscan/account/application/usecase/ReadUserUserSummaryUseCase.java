package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.response.ReadUserUserSummaryResponseDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

import java.util.UUID;

@UseCase
public interface ReadUserUserSummaryUseCase {
    /**
     * 3.2.1 유저 배송 정보 조회 유스케이스
     * @param accountId UUID
     * @return ReadUserSummaryResponseDto
     */
    ReadUserUserSummaryResponseDto execute(UUID accountId);
}
