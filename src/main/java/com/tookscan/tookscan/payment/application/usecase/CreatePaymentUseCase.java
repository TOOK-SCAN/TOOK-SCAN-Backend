package com.tookscan.tookscan.payment.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.payment.presentation.dto.request.CreatePaymentRequestDto;

@UseCase
public interface CreatePaymentUseCase {

    void execute(CreatePaymentRequestDto requestDto);
}
