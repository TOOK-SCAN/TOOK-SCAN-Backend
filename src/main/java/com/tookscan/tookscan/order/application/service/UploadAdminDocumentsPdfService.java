package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    private final DocumentRepository documentRepository;
    private final PdfRepository pdfRepository;

    private final OrderService orderService;

    private final S3Util s3Util;

    @Override
    public void execute(Long documentId, MultipartFile file) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        String pdfUrl = s3Util.uploadPdf(document, file);

        Pdf pdf = Pdf.builder()
                .pdfUrl(pdfUrl)
                .pdfCreatedAt(LocalDateTime.now())
                .document(document)
                .build();

        pdfRepository.save(pdf);
        document.getPdfs().add(pdf);

        // PDF 저장 후 주문 상태 업데이트
        updateOrderStatusBasedOnPdfStorage(document.getOrder());
    }

    private void updateOrderStatusBasedOnPdfStorage(Order order) {
        boolean hasAnyPdf = order.getDocuments().stream()
                .anyMatch(doc -> !doc.getPdfs().isEmpty());

        boolean allDocumentsHavePdf = order.getDocuments().stream()
                .noneMatch(doc -> doc.getPdfs().isEmpty());

        if (allDocumentsHavePdf) {
            // 모든 문서에 PDF가 1개 이상씩 저장되었을 경우 스캔 완료 상태로 변경
            orderService.startScan(order);
        } else if (hasAnyPdf) {
            // PDF가 저장된 문서가 하나라도 있을 경우 스캔중 상태로 변경
            orderService.completeScan(order);
        }
    }
}
