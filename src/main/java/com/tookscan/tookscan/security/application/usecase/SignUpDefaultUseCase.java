package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.application.dto.request.SignUpDefaultRequestDto;

@UseCase
public interface SignUpDefaultUseCase {
    /**
     * 임시 회원가입 유스케이스
     * @param requestDto 점주 회원가입 요청 DTO With Token
     * @return TemporaryJsonWebTokenDto
     */
     void execute(SignUpDefaultRequestDto requestDto);
}
