package com.tookscan.tookscan.term.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.term.presentation.dto.response.ReadUserTermOverviewResponseDto;

@UseCase
public interface ReadUserTermOverviewUseCase {
    /**
     * 8.2.2 약관 조회
     * @param type 조회할 약관 타입
     * @return 약관 조회 응답 Dto
     */
    ReadUserTermOverviewResponseDto execute(String type);
}
