package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.ReadSerialIdAndProviderRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReadSerialIdAndProviderResponseDto;

@UseCase
public interface ReadSerialIdAndProviderUseCase {

    ReadSerialIdAndProviderResponseDto execute(ReadSerialIdAndProviderRequestDto requestDto);
}
