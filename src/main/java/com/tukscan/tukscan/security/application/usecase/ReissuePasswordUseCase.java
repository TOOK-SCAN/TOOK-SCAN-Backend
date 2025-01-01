package com.tukscan.tukscan.security.application.usecase;

import com.tukscan.tukscan.core.annotation.bean.UseCase;
import com.tukscan.tukscan.security.application.dto.request.ReissuePasswordRequestDto;
import com.tukscan.tukscan.security.application.dto.response.ReissuePasswordResponseDto;

@UseCase
public interface ReissuePasswordUseCase {

        /**
        * 비밀번호 재발급
        * @param reissuePasswordRequestDto 시리얼 ID, 전화번호
        */
        ReissuePasswordResponseDto execute(ReissuePasswordRequestDto reissuePasswordRequestDto);
}
