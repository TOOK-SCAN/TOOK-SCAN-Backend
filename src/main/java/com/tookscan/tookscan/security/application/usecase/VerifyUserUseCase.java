package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.presentation.dto.request.VerifyUserRequestDto;

@UseCase
public interface VerifyUserUseCase {
    DefaultJsonWebTokenDto execute(VerifyUserRequestDto requestDto);
}
