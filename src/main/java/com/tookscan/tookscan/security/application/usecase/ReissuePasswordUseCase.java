package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.ReissuePasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReissuePasswordResponseDto;

@UseCase
public interface ReissuePasswordUseCase {

        /**
        * 비밀번호 재발급
        * @param reissuePasswordRequestDto 시리얼 ID, 전화번호
        */
        ReissuePasswordResponseDto execute(ReissuePasswordRequestDto reissuePasswordRequestDto);
}
