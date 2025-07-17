package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusCompanyArrivedRequestDto;

@UseCase
public interface UpdateAdminOrdersStatusCompanyArrivedUseCase {
    void execute(UpdateAdminOrdersStatusCompanyArrivedRequestDto requestDto);
}