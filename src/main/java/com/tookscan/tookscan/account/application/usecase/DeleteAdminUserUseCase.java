package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.request.DeleteAdminUserRequestDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface DeleteAdminUserUseCase {
    /**
     * 3.5.2 (관리자) 유저 삭제
     */
    void execute(DeleteAdminUserRequestDto requestDto);
}
