package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.application.dto.request.UpdateAdminUserRequestDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

import java.util.UUID;

@UseCase
public interface UpdateAdminUserUseCase {
    /**
     * 3.4.2 (관리자) 유저 정보 수정 유스케이스
     * @param requestDto 요청 DTO
     * @param userId 요청자 ID
     */
    void execute(UpdateAdminUserRequestDto requestDto, UUID userId);
}
