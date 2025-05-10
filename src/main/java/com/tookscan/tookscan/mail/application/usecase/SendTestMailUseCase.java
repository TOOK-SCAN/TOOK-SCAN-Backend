package com.tookscan.tookscan.mail.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.mail.presentation.dto.request.SendTestMailRequestDto;

@UseCase
public interface SendTestMailUseCase {
    void execute(SendTestMailRequestDto requestDto);
}
