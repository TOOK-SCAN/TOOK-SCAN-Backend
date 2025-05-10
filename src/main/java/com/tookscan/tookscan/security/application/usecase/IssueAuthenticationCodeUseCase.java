package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.IssueAuthenticationCodeRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.IssueAuthenticationCodeResponseDto;

@UseCase
public interface IssueAuthenticationCodeUseCase {
    /**
     * 휴대폰 인증 코드 발급
     * @param requestDto 휴대폰 인증 요청 DTO
     * @return 휴대폰 인증 응답 DTO
     */
    IssueAuthenticationCodeResponseDto execute(IssueAuthenticationCodeRequestDto requestDto);
}
