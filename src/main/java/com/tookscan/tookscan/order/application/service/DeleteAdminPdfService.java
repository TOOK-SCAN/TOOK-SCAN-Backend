package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.DeleteAdminPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.service.PdfService;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAdminPdfService implements DeleteAdminPdfUseCase {
    private final PdfRepository pdfRepository;
    private final S3Util s3Util;
    private final PdfService pdfService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "delete pdf",
        userType = "Admin"
    )
    public void execute(Long pdfId) {
        Pdf pdf = pdfRepository.findByIdOrElseThrow(pdfId);
        Document document = pdf.getDocument();
        
        // S3에서 PDF 파일 삭제
        s3Util.deletePdfFromS3(pdf);
        
        // 데이터베이스에서 PDF 엔티티 삭제
        pdfRepository.deleteByIdOrElseThrow(pdfId);
        
        // PDF 삭제 후 해당 Document의 PDF 파일명들을 재정렬
        pdfService.reorderPdfFileNames(document, s3Util::renameS3ObjectAndGetUrl);
        
        LogContext.put("pdf_id", pdfId);
        LogContext.put("document_id", document.getId());
    }
}
