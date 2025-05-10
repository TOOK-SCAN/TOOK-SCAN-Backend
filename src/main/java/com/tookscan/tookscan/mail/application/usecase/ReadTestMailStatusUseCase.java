package com.tookscan.tookscan.mail.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.mail.presentation.dto.response.ReadTestMailStatusResponseDto;

@UseCase
public interface ReadTestMailStatusUseCase {

    ReadTestMailStatusResponseDto execute(String email);
}
