package com.tookscan.tookscan.term.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.term.application.dto.request.CreateAdminTermRequestDto;

@UseCase
public interface CreateAdminTermUseCase {
    /**
     * 8.1.1 (관리자) 약관 추가 유스케이스
     * @param requestDto 요청 DTO
     */
    void execute(CreateAdminTermRequestDto requestDto);
}
