package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.VerifyPasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.ValidationResponseDto;

import java.util.UUID;

@UseCase
public interface VerifyPasswordUseCase {
    ValidationResponseDto execute(UUID accountId, VerifyPasswordRequestDto requestDto);
}
