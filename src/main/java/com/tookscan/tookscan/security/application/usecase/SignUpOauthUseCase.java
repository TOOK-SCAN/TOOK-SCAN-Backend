package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpOauthRequestDto;

@UseCase
public interface SignUpOauthUseCase {
    /**
     * 소셜 로그인 회원가입 유스케이스
     * @param temporaryToken 임시 토큰
     * @param requestDto 소셜 로그인 회원가입 요청 DTO
     */
    DefaultJsonWebTokenDto execute(String temporaryToken, SignUpOauthRequestDto requestDto);
}
