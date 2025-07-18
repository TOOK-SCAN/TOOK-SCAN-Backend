package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ValidateAdminPdfResponseDto;
import org.springframework.web.multipart.MultipartFile;

@UseCase
public interface ValidateAdminPdfUseCase {
    ValidateAdminPdfResponseDto execute(MultipartFile file);
}
