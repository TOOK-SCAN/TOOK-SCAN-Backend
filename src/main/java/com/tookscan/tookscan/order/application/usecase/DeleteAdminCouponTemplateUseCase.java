package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;

@UseCase
public interface DeleteAdminCouponTemplateUseCase {
    void execute(Long id);
}
