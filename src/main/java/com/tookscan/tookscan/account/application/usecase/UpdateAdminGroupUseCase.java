package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.request.UpdateAdminGroupRequestDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface UpdateAdminGroupUseCase {
    /**
     * 3.2.2 (관리자) 그룹 수정 유스케이스
     * @param requestDto 그룹 이름 Dto
     * @param groupId 그룹 ID
     */
    void execute(UpdateAdminGroupRequestDto requestDto, Long groupId);
}
