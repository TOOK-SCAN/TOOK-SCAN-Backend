package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusCancelRequestDto;

@UseCase
public interface UpdateAdminOrdersStatusCancelUseCase {
    void execute(UpdateAdminOrdersStatusCancelRequestDto requestDto);
}
