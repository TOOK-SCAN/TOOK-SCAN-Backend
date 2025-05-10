package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.ValidateAuthenticationCodeRequestDto;

@UseCase
public interface ValidateAuthenticationCodeUseCase {

    /**
     * 전화번호 인증 코드 검증
     * @param requestDto 전화번호 인증 요청 DTO
     */
    void execute(ValidateAuthenticationCodeRequestDto requestDto);
}
