package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.event.AdminPdfUploadRequestedEvent;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.service.PdfService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import com.tookscan.tookscan.order.presentation.dto.response.UploadAdminDocumentsPdfResponseDto;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 관리자 문서 PDF 업로드 서비스 (통합된 실용적 버전)
 * <p>
 * 역할: PDF 업로드 요청을 받아 워터마크 처리 후 저장하는 전체 워크플로우 관리
 * <p>
 * 워크플로우: 1. 기본 검증 및 파일 검사 (동기) 2. 각 파일별로 비동기 처리: - 워터마크 처리, S3 업로드, PDF 엔티티 저장, 주문 상태 업데이트 (단일 내부 메서드)
 * <p>
 * 장점: - 통합된 구조 (모든 PDF 처리 로직이 하나의 서비스에 집중) - 실용적인 구조 (과도한 분리 없음) - 유지보수 용이성 (관련 로직이 한 곳에 모여있음) - 비동기 처리로 성능 향상
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    // Repositories and Domain Services
    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final PdfService pdfService;
    private final ApplicationEventPublisher eventPublisher;
    private final PdfRepository pdfRepository;

    // 요청 파일 사전 준비용 내부 클래스
    private static class PreparedUploadFile {
        private final String originalFileName;
        private final MultipartFile file;

        private PreparedUploadFile(String originalFileName, MultipartFile file) {
            this.originalFileName = originalFileName;
            this.file = file;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public MultipartFile getFile() {
            return file;
        }
    }

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
    public UploadAdminDocumentsPdfResponseDto execute(Long documentId, List<MultipartFile> files) {
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

        // 4. 요청 내 중복 파일명 선제 차단 (for문 전에 전체 검사) + 파일 객체 보관
        Set<String> requestDuplicateGuard = new HashSet<>();
        List<PreparedUploadFile> preparedFiles = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                originalFileName = "unnamed.pdf";
            }
            if (!requestDuplicateGuard.add(originalFileName)) {
                throw new RuntimeException("동일 요청 내에서 중복된 파일명이 존재합니다: " + originalFileName);
            }
            preparedFiles.add(new PreparedUploadFile(originalFileName, file));
        }

        List<Pdf> pdfs = new ArrayList<>();

        // 5. 각 파일을 비동기로 처리
        for (PreparedUploadFile prepared : preparedFiles) {
            String originalFileName = prepared.getOriginalFileName();
            MultipartFile file = prepared.getFile();
            try {
                // 파일명 중복 검증 (메인 트랜잭션에서)
                pdfService.validateUniqueFilename(document, originalFileName);

                // S3에 저장할 고유 파일명 생성
                String extension = StringUtils.getFilenameExtension(originalFileName);
                if (extension == null || extension.isBlank()) {
                    extension = "pdf";
                }
                String storedFileName = UUID.randomUUID() + "." + extension;

                // MultipartFile을 byte[]로 변환
                byte[] fileContent = file.getBytes();

                // 업로드 태스크 ID 생성 및 사전 저장 (PENDING)
                Pdf preCreated = Pdf.builder()
                        .pdfUrlForAdmin("")
                        .name(originalFileName)
                        .storedFileName(storedFileName)
                        .isChecked(false)
                        .document(document)
                        .uploadStatus(EPdfUploadStatus.PENDING)
                        .build();
                Pdf pdf = pdfRepository.save(preCreated);
                pdfs.add(pdf);

                // 비동기 처리 시작 - 이벤트 퍼블리시 (AFTER_COMMIT에 비동기 핸들링)
                AdminPdfUploadRequestedEvent event = AdminPdfUploadRequestedEvent.builder()
                        .pdfId(preCreated.getId())
                        .documentId(documentId)
                        .fileContent(fileContent)
                        .originalFileName(originalFileName)
                        .storedFileName(storedFileName)
                        .userName(userName)
                        .userPhone(userPhone)
                        .orderNumber(orderNumber)
                        .orderCreatedAt(orderCreatedAt)
                        .orderId(order.getId())
                        .aesKey(aesKeyString.getBytes(StandardCharsets.UTF_8))
                        .build();
                eventPublisher.publishEvent(event);

                log.info("Successfully initiated async processing for file: {} (document ID: {})",
                        originalFileName, documentId);

            } catch (IOException e) {
                log.error("Failed to read file content for file: {} (document ID: {}). Error: {}",
                        originalFileName, documentId, e.getMessage(), e);
                throw new RuntimeException("파일 읽기 실패: " + originalFileName, e);
            }
        }

        // 6. 로깅 (즉시 응답)
        LogContext.put("document_id", documentId);
        LogContext.put("order_id", order.getId());
        LogContext.put("uploaded_files_count", files.size());

        log.info("PDF upload process completed for document ID: {}. {} files are being processed asynchronously.",
                documentId, files.size());
        return UploadAdminDocumentsPdfResponseDto.fromEntity(pdfs);
    }


}
