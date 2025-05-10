package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.request.CreateAdminUserGroupRequestDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface CreateAdminUserGroupUseCase {
    /**
     * 3.1.2 (관리자) 사용자 그룹 지정
     *
     * @param requestDto 요청 DTO
     */
    void execute(CreateAdminUserGroupRequestDto requestDto);
}
