package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusRequestDto;

@UseCase
public interface UpdateAdminOrdersStatusUseCase {
    void execute(UpdateAdminOrdersStatusRequestDto requestDto);
}
