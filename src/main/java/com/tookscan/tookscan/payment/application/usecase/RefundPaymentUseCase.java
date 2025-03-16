package com.tookscan.tookscan.payment.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface RefundPaymentUseCase {
    void execute(Long paymentId);
}
