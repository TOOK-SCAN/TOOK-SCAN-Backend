package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.request.AdminSignUpDefaultRequestDto;

@UseCase
public interface AdminSignUpDefaultUseCase {

    void execute(AdminSignUpDefaultRequestDto requestDto);
}
