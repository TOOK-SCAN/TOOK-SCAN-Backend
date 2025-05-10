package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.ExportAdminDeliveriesRequestDto;

@UseCase
public interface ExportAdminDeliveriesUseCase {
    byte[] execute(ExportAdminDeliveriesRequestDto requestDto);
}
