package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.application.dto.OauthJsonWebTokenDto;
import com.tookscan.tookscan.security.info.CustomTemporaryUserPrincipal;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;

@UseCase
public interface LoginOauthUseCase {
    /**
     * Security에서 사용되는 Social Login 유스케이스
     * @param principal CustomTemporaryUserPrincipal
     */

    OauthJsonWebTokenDto execute(CustomTemporaryUserPrincipal principal);

    /**
     * Security에서 사용되는 Social Login 유스케이스
     * @param principal CustomUserPrincipal
     */

    OauthJsonWebTokenDto execute(CustomUserPrincipal principal);

}
