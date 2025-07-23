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
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public void execute(Long documentId, List<MultipartFile> files) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        Order order = orderRepository.findByIdOrElseThrow(document.getOrder().getId());
        User user = userRepository.findByIdOrElseThrow(order.getUser().getId());

        // 병렬 처리용 데이터 준비 (엔티티에서 필요한 값들만 추출)
        String userName = user.getName();
        String userPhone = user.getPhoneNumber();
        String orderNumber = order.getOrderNumber();
        String orderCreatedAt = DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt());

        // 병렬로 워터마킹 및 S3 업로드 처리 (트랜잭션 외부에서 실행)
        List<String> pdfUrls = processFilesInParallel(document, files, userName, userPhone, orderNumber, orderCreatedAt);

        // DB 저장은 순차적으로 (트랜잭션 내에서)
        for (String pdfUrl : pdfUrls) {
            Pdf pdf = Pdf.builder()
                    .pdfUrl(pdfUrl)
                    .pdfCreatedAt(LocalDateTime.now())
                    .document(document)
                    .build();
            
            pdfRepository.save(pdf);
            document.getPdfs().add(pdf);
        }

        updateOrderStatusBasedOnPdfStorage(order);
    }

    private List<String> processFilesInParallel(Document document, List<MultipartFile> files, 
                                               String userName, String userPhone, String orderNumber, String orderCreatedAt) {
        // 병렬로 워터마킹 및 S3 업로드 처리
        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> 
                    processFileToUrl(document, file, userName, userPhone, orderNumber, orderCreatedAt)))
                .toList();

        // 모든 병렬 작업 완료 대기
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private String processFileToUrl(Document document, MultipartFile file, 
                                   String userName, String userPhone, String orderNumber, String orderCreatedAt) {
        File watermarkedPdf = PdfWatermarkUtil.embedWatermark(
                file,
                userName,
                userPhone,
                orderNumber,
                orderCreatedAt,
                aesKeyString.getBytes()
        );

        return s3Util.uploadPdf(document, watermarkedPdf);
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
