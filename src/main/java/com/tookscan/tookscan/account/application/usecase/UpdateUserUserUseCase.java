package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.request.UpdateUserUserRequestDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

import java.util.UUID;

@UseCase
public interface UpdateUserUserUseCase {
    /**
     * 유저 정보 수정 유스케이스
     * @param accountId UUID
     * @param requestDto UpdateUserRequestDto
     */
    void execute(UUID accountId, UpdateUserUserRequestDto requestDto);
}
