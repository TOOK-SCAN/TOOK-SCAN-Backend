package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.request.UploadAdminDocumentsPdfWithPresignedUrlRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.UploadAdminDocumentsPdfWithPresignedUrlResponseDto;

@UseCase
public interface UploadAdminDocumentsPdfWithPresignedUrlUseCase {
    UploadAdminDocumentsPdfWithPresignedUrlResponseDto execute(Long documentId,
                                                               UploadAdminDocumentsPdfWithPresignedUrlRequestDto requestDto);
}
