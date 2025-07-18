package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.core.utility.PdfWatermarkUtil;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    @Value("${aes-key}")
    private String aesKeyString;

    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PdfRepository pdfRepository;

    private final OrderService orderService;

    private final S3Util s3Util;

    @Override
    @Transactional
    public void execute(Long documentId, MultipartFile file) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);

        Order order = orderRepository.findByIdOrElseThrow(document.getOrder().getId());

        User user = userRepository.findByIdOrElseThrow(order.getUser().getId());

        File watermarkedPdf = PdfWatermarkUtil.embedWatermark(
                file,
                user.getName(),
                user.getPhoneNumber(),
                order.getOrderNumber(),
                DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()),
                aesKeyString.getBytes()
        );

        String pdfUrl = s3Util.uploadPdf(document, watermarkedPdf);

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
            orderService.completeScan(order);
        } else if (hasAnyPdf) {
            // PDF가 저장된 문서가 하나라도 있을 경우 스캔중 상태로 변경
            orderService.startScan(order);
        }
    }
}
