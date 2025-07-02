package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.DeleteAccountRequestDto;

import java.util.UUID;

@UseCase
public interface DeleteAccountUseCase {

    /**
     * 계정 탈퇴
     * @param accountId 계정 ID
     */
    void execute(UUID accountId, DeleteAccountRequestDto requestDto);
}
