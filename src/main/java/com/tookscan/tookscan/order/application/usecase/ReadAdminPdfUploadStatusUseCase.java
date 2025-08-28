package com.tookscan.tookscan.order.application.usecase;


import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminPdfUploadStatusResponseDto;

@UseCase
public interface ReadAdminPdfUploadStatusUseCase {
    ReadAdminPdfUploadStatusResponseDto execute(Long pdfId);
}