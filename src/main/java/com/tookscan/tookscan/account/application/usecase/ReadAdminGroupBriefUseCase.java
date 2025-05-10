package com.tookscan.tookscan.account.application.usecase;

import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminGroupBriefResponseDto;
import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface ReadAdminGroupBriefUseCase {
    /**
     * 3.2.5 (관리자) 그룹 간단 정보 조회 유스케이스
     * @return ReadAdminGroupBriefResponseDto
     */
    ReadAdminGroupBriefResponseDto execute();
}
