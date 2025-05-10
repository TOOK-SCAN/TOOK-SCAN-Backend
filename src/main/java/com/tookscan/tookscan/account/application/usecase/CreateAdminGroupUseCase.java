package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.request.CreateAdminGroupRequestDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface CreateAdminGroupUseCase {
    /**
     * 3.1.1 (관리자) 그룹 만들기 유스케이스
     * @param requestDto 그룹 생성 요청 DTO
     */
    void execute(CreateAdminGroupRequestDto requestDto);
}
