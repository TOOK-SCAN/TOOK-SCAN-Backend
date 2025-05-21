package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpDefaultRequestDto;

@UseCase
public interface SignUpDefaultUseCase {
    /**
     * 임시 회원가입 유스케이스
     * @param requestDto 점주 회원가입 요청 DTO With Token
     * @return TemporaryJsonWebTokenDto
     */
     DefaultJsonWebTokenDto execute(SignUpDefaultRequestDto requestDto);
}
