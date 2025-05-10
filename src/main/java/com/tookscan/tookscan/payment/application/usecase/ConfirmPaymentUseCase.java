package com.tookscan.tookscan.payment.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.payment.presentation.dto.request.ConfirmPaymentRequestDto;

@UseCase
public interface ConfirmPaymentUseCase {
    void execute(ConfirmPaymentRequestDto requestDto);
}
