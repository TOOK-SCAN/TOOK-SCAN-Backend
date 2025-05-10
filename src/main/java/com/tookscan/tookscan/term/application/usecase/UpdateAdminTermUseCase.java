package com.tookscan.tookscan.term.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.term.presentation.dto.request.UpdateAdminTermRequestDto;

@UseCase
public interface UpdateAdminTermUseCase {
    /**
     * 8.4.1 (관리자) 약관 수정 유스케이스
     */
    void execute(UpdateAdminTermRequestDto requestDto);
}
