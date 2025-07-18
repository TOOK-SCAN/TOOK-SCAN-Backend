package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusRecoveryOptionRequestDto;

import java.util.UUID;

@UseCase
public interface UpdateAdminOrdersStatusRecoveryOptionUseCase {
    void execute(UUID accountId, UpdateAdminOrdersStatusRecoveryOptionRequestDto requestDto);
}