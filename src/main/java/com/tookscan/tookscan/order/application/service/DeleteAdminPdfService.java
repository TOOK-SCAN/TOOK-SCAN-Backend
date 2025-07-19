package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.DeleteAdminPdfUseCase;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAdminPdfService implements DeleteAdminPdfUseCase {
    private final PdfRepository pdfRepository;

    @Override
    public void execute(Long pdfId) {
        pdfRepository.deleteByIdOrElseThrow(pdfId);
    }
}
