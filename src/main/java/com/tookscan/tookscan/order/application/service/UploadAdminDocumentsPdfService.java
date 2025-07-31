package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.core.utility.PdfWatermarkUtil;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    @Value("${aes-key}")
    private String aesKeyString;

    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PdfRepository pdfRepository;

    private final OrderService orderService;

    private final S3Util s3Util;

    private final Executor fileProcessingExecutor;
    
    public UploadAdminDocumentsPdfService(
            DocumentRepository documentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository,
            PdfRepository pdfRepository,
            OrderService orderService,
            S3Util s3Util,
            @Qualifier("fileProcessingTaskExecutor") Executor fileProcessingExecutor) {
        this.documentRepository = documentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.pdfRepository = pdfRepository;
        this.orderService = orderService;
        this.s3Util = s3Util;
        this.fileProcessingExecutor = fileProcessingExecutor;
    }

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "upload pdfs",
        userType = "Admin"
    )
    public void execute(Long documentId, List<MultipartFile> files) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        Order order = orderRepository.findByIdOrElseThrow(document.getOrder().getId());
        User user = userRepository.findByIdOrElseThrow(order.getUser().getId());

        // 주문 상태 검증
        List<EOrderStatus> validStatuses = List.of(
                EOrderStatus.PAYMENT_COMPLETED,
                EOrderStatus.SCAN_IN_PROGRESS,
                EOrderStatus.SCAN_COMPLETED
        );

        orderService.validateOrderStatuses(order, validStatuses, ErrorCode.INVALID_ORDER_STATUS);

        // 병렬 처리용 데이터 준비 (엔티티에서 필요한 값들만 추출)
        String userName = user.getName();
        String userPhone = user.getPhoneNumber();
        String orderNumber = order.getOrderNumber();
        String orderCreatedAt = DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt());

        // DB 저장 및 S3 업로드 (트랜잭션 내에서)
        for (MultipartFile file : files) {
            Pdf pdf = Pdf.builder()
                    .pdfUrl("temp") // 임시 URL, 실제 업로드 후 업데이트
                    .pdfCreatedAt(LocalDateTime.now())
                    .document(document)
                    .build();

            // PDF 엔티티를 먼저 저장하여 ID 생성
            pdfRepository.save(pdf);
            document.getPdfs().add(pdf);

            // PDF ID를 이용하여 실제 S3 업로드 및 URL 업데이트
            String pdfUrl = processFileToUrlWithPdfId(document, file, pdf.getId(), userName, userPhone, orderNumber,
                    orderCreatedAt);
            pdf.updatePdfUrl(pdfUrl);
        }

        updateOrderStatusBasedOnPdfStorage(order);
        
        LogContext.put("document_id", documentId);
        LogContext.put("order_id", order.getId());
        LogContext.put("uploaded_files_count", files.size());
    }

    private String processFileToUrlWithPdfId(Document document, MultipartFile file, Long pdfId,
                                             String userName, String userPhone, String orderNumber,
                                             String orderCreatedAt) {
        File watermarkedPdf = PdfWatermarkUtil.embedWatermark(
                file,
                userName,
                userPhone,
                orderNumber,
                orderCreatedAt,
                aesKeyString.getBytes()
        );

        return s3Util.uploadPdf(document, watermarkedPdf, pdfId);
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
