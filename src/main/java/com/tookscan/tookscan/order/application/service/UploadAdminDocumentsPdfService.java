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
import com.tookscan.tookscan.order.domain.service.PdfService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 관리자 문서 PDF 업로드 서비스 (통합된 실용적 버전)
 * 
 * 역할: PDF 업로드 요청을 받아 워터마크 처리 후 저장하는 전체 워크플로우 관리
 * 
 * 워크플로우:
 * 1. 기본 검증 및 파일 검사 (동기)
 * 2. 각 파일별로 비동기 처리:
 *    - 워터마크 처리, S3 업로드, PDF 엔티티 저장, 주문 상태 업데이트 (단일 내부 메서드)
 * 
 * 장점:
 * - 통합된 구조 (모든 PDF 처리 로직이 하나의 서비스에 집중)
 * - 실용적인 구조 (과도한 분리 없음)  
 * - 유지보수 용이성 (관련 로직이 한 곳에 모여있음)
 * - 비동기 처리로 성능 향상
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    // Repositories and Domain Services
    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PdfRepository pdfRepository;
    private final OrderService orderService;
    private final PdfService pdfService;
    private final S3Util s3Util;
    
    // 워터마크 처리를 위한 AES 키 설정
    @Value("${aes-key}")
    private String aesKeyString;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "upload pdfs",
        userType = "Admin"
    )
    public void execute(Long documentId, List<MultipartFile> files) {
        log.info("Starting PDF upload process for document ID: {}, file count: {}", documentId, files.size());
        
        // 1. 기본 검증 및 엔티티 조회 (메인 트랜잭션에서)
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        Order order = orderRepository.findByIdOrElseThrow(document.getOrder().getId());
        User user = userRepository.findByIdOrElseThrow(order.getUser().getId());

        // 2. 주문 상태 검증
        List<EOrderStatus> validStatuses = List.of(
                EOrderStatus.PAYMENT_COMPLETED,
                EOrderStatus.SCAN_IN_PROGRESS,
                EOrderStatus.SCAN_COMPLETED
        );

        orderService.validateOrderStatuses(order, validStatuses, ErrorCode.INVALID_ORDER_STATUS);

        // 3. 워터마크용 메타데이터 준비
        String userName = user.getName();
        String userPhone = user.getPhoneNumber();
        String orderNumber = order.getOrderNumber();
        String orderCreatedAt = DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt());

        // 4. 각 파일을 비동기로 처리
        for (MultipartFile file : files) {
            try {
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null || originalFileName.trim().isEmpty()) {
                    originalFileName = "unnamed.pdf";
                }

                // 파일명 중복 검증 (메인 트랜잭션에서)
                pdfService.validateUniqueFilename(document, originalFileName);
                
                // S3에 저장할 고유 파일명 생성
                String extension = StringUtils.getFilenameExtension(originalFileName);
                String storedFileName = UUID.randomUUID() + "." + extension;

                // MultipartFile을 byte[]로 변환
                byte[] fileContent = file.getBytes();
                
                // 비동기 처리 시작
                processFileAsync(documentId, fileContent, originalFileName, storedFileName,
                               userName, userPhone, orderNumber, orderCreatedAt, order.getId());
                
                log.info("Successfully initiated async processing for file: {} (document ID: {})", 
                        originalFileName, documentId);
                
            } catch (IOException e) {
                log.error("Failed to read file content for file: {} (document ID: {}). Error: {}", 
                         file.getOriginalFilename(), documentId, e.getMessage(), e);
                throw new RuntimeException("파일 읽기 실패: " + file.getOriginalFilename(), e);
            }
        }
        
        // 5. 로깅 (즉시 응답)
        LogContext.put("document_id", documentId);
        LogContext.put("order_id", order.getId());
        LogContext.put("uploaded_files_count", files.size());
        
        log.info("PDF upload process completed for document ID: {}. {} files are being processed asynchronously.", 
                documentId, files.size());
    }

    /**
     * 단일 파일에 대한 비동기 처리
     * 
     * 워크플로우: 워터마크 처리 → S3 업로드 → PDF 엔티티 저장 → 주문 상태 업데이트 (통합 처리)
     */
    @Async("fileProcessingTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processFileAsync(Long documentId, byte[] fileContent, String originalFileName, String storedFileName,
                                String userName, String userPhone, String orderNumber, String orderCreatedAt, Long orderId) {
        
        log.info("Starting async PDF processing for file: {} (document ID: {})", originalFileName, documentId);
        
        File watermarkedFile = null;
        
        try {
            // 1단계: 워터마크 처리 (내부 로직)
            CustomMultipartFile multipartFile = new CustomMultipartFile("file", originalFileName, "application/pdf", fileContent);
            byte[] aesKey = aesKeyString.getBytes();
            watermarkedFile = PdfWatermarkUtil.embedWatermark(multipartFile, userName, userPhone, orderNumber, orderCreatedAt, aesKey);
            log.debug("Watermark processing completed for file: {}", originalFileName);
            
            // 2단계: 엔티티 재조회 (새로운 트랜잭션에서)
            Document document = documentRepository.findByIdOrElseThrow(documentId);
            
            // 3단계: S3 업로드
            String pdfUrl = s3Util.uploadPdfAndGetSignedUrlForAdmin(
                    document, watermarkedFile, storedFileName, originalFileName);
            log.debug("S3 upload completed for file: {}", originalFileName);
            
            // 4단계: PDF 엔티티 저장
            Pdf pdf = Pdf.builder()
                    .pdfUrlForAdmin(pdfUrl)
                    .name(originalFileName)
                    .storedFileName(storedFileName)
                    .isChecked(false)
                    .document(document)
                    .build();
            
            pdfRepository.save(pdf);
            document.getPdfs().add(pdf);
            log.debug("PDF entity saved for file: {}", originalFileName);
            
            // 5단계: 주문 상태 업데이트 (Race Condition 방지)
            updateOrderStatusAfterPdfProcessing(orderId);
            
            log.info("Successfully completed PDF processing for file: {} (document ID: {})", originalFileName, documentId);
            
        } catch (Exception e) {
            log.error("Failed to process PDF file: {} (document ID: {}). Error: {}", 
                     originalFileName, documentId, e.getMessage(), e);
            
            // 실패한 경우에도 주문 상태 업데이트 시도 (부분 업로드 반영)
            try {
                updateOrderStatusAfterPdfProcessing(orderId);
            } catch (Exception statusUpdateException) {
                log.error("Failed to update order status after processing failure for file: {} (order ID: {}). Error: {}", 
                         originalFileName, orderId, statusUpdateException.getMessage(), statusUpdateException);
            }
            
            throw new RuntimeException("PDF 처리 실패: " + originalFileName, e);
            
        } finally {
            // 워터마크된 임시 파일 정리
            if (watermarkedFile != null && watermarkedFile.exists()) {
                if (watermarkedFile.delete()) {
                    log.debug("Cleaned up watermarked temp file: {}", watermarkedFile.getName());
                } else {
                    log.warn("Failed to delete watermarked temp file: {}", watermarkedFile.getName());
                }
            }
        }
    }

    /**
     * PDF 처리 완료 후 주문 상태 업데이트
     * 동시성 제어를 위해 별도 트랜잭션으로 분리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOrderStatusAfterPdfProcessing(Long orderId) {
        log.debug("Updating order status for order ID: {}", orderId);
        
        try {
            // TODO: 비관적 락 적용 시 OrderRepository에 findByIdWithLock 메서드 추가 필요
            // @Lock(LockModeType.PESSIMISTIC_WRITE)
            // @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
            Order order = orderRepository.findByIdOrElseThrow(orderId);
            
            // 주문 상태 업데이트 로직
            boolean hasAnyPdf = order.getDocuments().stream()
                    .anyMatch(doc -> !doc.getPdfs().isEmpty());
                    
            boolean allDocumentsHavePdf = order.getDocuments().stream()
                    .noneMatch(doc -> doc.getPdfs().isEmpty());
            
            if (allDocumentsHavePdf) {
                // 모든 문서에 PDF가 1개 이상씩 저장되었을 경우 스캔 완료 상태로 변경
                log.info("All documents have PDFs, completing scan for order ID: {}", orderId);
                orderService.completeScan(order);
            } else if (hasAnyPdf) {
                // PDF가 저장된 문서가 하나라도 있을 경우 스캔중 상태로 변경
                log.info("Some documents have PDFs, starting scan for order ID: {}", orderId);
                orderService.startScan(order);
            }
            
            log.debug("Successfully updated order status for order ID: {}", orderId);
            
        } catch (Exception e) {
            log.error("Failed to update order status for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }

    /**
     * byte[] 데이터를 MultipartFile로 변환하기 위한 커스텀 구현체
     */
    private static class CustomMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content != null ? content.clone() : new byte[0];
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content.clone();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("CustomMultipartFile does not support transferTo operation");
        }
    }

}
