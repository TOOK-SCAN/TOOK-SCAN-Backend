package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.response.ReadUserUserDetailResponseDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

import java.util.UUID;

@UseCase
public interface ReadUserUserDetailUseCase {
    /**
     * 3.2.2 유저 상세 정보 조회 유스케이스
     * @param accountId UUID
     * @return ReadUserDetailResponseDto
     */
    ReadUserUserDetailResponseDto execute(UUID accountId);
}
