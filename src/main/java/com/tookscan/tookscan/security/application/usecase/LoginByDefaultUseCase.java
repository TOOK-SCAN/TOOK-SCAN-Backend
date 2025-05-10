package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;

@UseCase
public interface LoginByDefaultUseCase {

    /**
     * Security에서 사용되는 Login 유스케이스
     * @param principal UserPrincipal
     * @param jsonWebTokenDto DefaultJsonWebTokenDto
     */
    void execute(CustomUserPrincipal principal, DefaultJsonWebTokenDto jsonWebTokenDto);
}
