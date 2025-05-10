package com.tookscan.tookscan.term.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.term.presentation.dto.request.CreateAdminTermRequestDto;
import com.tookscan.tookscan.term.presentation.dto.response.CreateAdminTermResponseDto;

@UseCase
public interface CreateAdminTermUseCase {
    /**
     * 8.1.1 (관리자) 약관 추가 유스케이스
     * @param requestDto 요청 DTO
     */
    CreateAdminTermResponseDto execute(CreateAdminTermRequestDto requestDto);
}
