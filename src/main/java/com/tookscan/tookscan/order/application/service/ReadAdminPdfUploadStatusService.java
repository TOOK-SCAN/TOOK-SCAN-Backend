package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminPdfUploadStatusUseCase;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminPdfUploadStatusResponseDto;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminPdfUploadStatusService implements ReadAdminPdfUploadStatusUseCase {

    private final PdfRepository pdfRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminPdfUploadStatusResponseDto execute(Long pdfId) {
        Pdf pdf = pdfRepository.findByIdOrElseThrow(pdfId);
        return ReadAdminPdfUploadStatusResponseDto.fromEntity(pdf);
    }
}