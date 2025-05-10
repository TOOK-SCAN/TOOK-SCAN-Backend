package com.tookscan.tookscan.term.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.term.presentation.dto.response.ReadAdminTermOverviewResponseDto;

@UseCase
public interface ReadAdminTermOverviewUseCase {

    ReadAdminTermOverviewResponseDto execute(String type);
}
