package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserDetailResponseDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

import java.util.UUID;

@UseCase
public interface ReadAdminUserDetailUseCase {
    /**
     * 3.2.3 관리자 사용자 상세 조회 유스케이스
     * @param accountId 사용자 ID
     * @return 사용자 상세 응답 DTO
     */
    ReadAdminUserDetailResponseDto execute(UUID accountId);
}
