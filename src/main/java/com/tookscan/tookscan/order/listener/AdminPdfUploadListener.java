package com.tookscan.tookscan.order.listener;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.PdfWatermarkUtil;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.service.S3UploadProgressListener;
import com.tookscan.tookscan.order.domain.event.PdfProgressEvent;
import com.tookscan.tookscan.order.application.usecase.UpdateOrderStatusAfterPdfProcessingUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.event.AdminPdfUploadRequestedEvent;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPdfUploadListener {

    private final DocumentRepository documentRepository;
    private final PdfRepository pdfRepository;
    private final S3Util s3Util;
    private final ApplicationEventPublisher eventPublisher;
    private final UpdateOrderStatusAfterPdfProcessingUseCase orderStatusUpdateService;

    @Async("fileProcessingTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = {AdminPdfUploadRequestedEvent.class})
    public void onEvent(AdminPdfUploadRequestedEvent event) {
        processFile(
                event.getPdfId(),
                event.getDocumentId(),
                event.getTempFilePath(),
                event.getOriginalFileName(),
                event.getStoredFileName(),
                event.getUserName(),
                event.getUserPhone(),
                event.getOrderNumber(),
                event.getOrderCreatedAt(),
                event.getOrderId(),
                event.getAesKey()
        );
    }

    public void processFile(
            Long pdfId,
            Long documentId,
            String tempFilePath,
            String originalFileName,
            String storedFileName,
            String userName,
            String userPhone,
            String orderNumber,
            String orderCreatedAt,
            Long orderId,
            byte[] aesKey
    ) {
        log.info("Starting async PDF processing for file: {} (document ID: {})", originalFileName, documentId);

        Pdf pdf = pdfRepository.findByIdOrElseThrow(pdfId);
        pdf.updateUploadStatus(EPdfUploadStatus.IN_PROGRESS);
        pdfRepository.save(pdf);

        File watermarkedFile = null;
        File originTempFile = null;
        try {
            originTempFile = new File(tempFilePath);
            if (!originTempFile.exists()) {
                throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "임시 업로드 파일을 찾을 수 없습니다.");
            }
            MultipartFile multipartFile = new TempFileMultipartFile("file", originalFileName, "application/pdf",
                    originTempFile);
            watermarkedFile = PdfWatermarkUtil.embedWatermark(multipartFile, userName, userPhone, orderNumber, orderCreatedAt, aesKey);
            log.debug("Watermark processing completed for file: {}", originalFileName);

            // 업로드/저장/상태변경은 IO 전용 실행기로 위임
            uploadAndPersistAsync(pdfId, documentId, watermarkedFile, storedFileName, originalFileName, orderId, originalFileName);
        } catch (Exception e) {
            log.error("Failed to process PDF file: {} (document ID: {}). Error: {}", originalFileName, documentId, e.getMessage(), e);
            // 워터마크 단계 실패 시에도 Pdf 상태를 FAILED로 업데이트
            try {
                pdf.updateUploadStatus(EPdfUploadStatus.FAILED);
                pdfRepository.save(pdf);
                PdfProgressEvent event = PdfProgressEvent.createCustomEvent(pdfId, "pdf_upload_status", EPdfUploadStatus.FAILED);
                eventPublisher.publishEvent(event);
            } catch (Exception ignored) {}
            try {
                orderStatusUpdateService.execute(orderId);
            } catch (Exception statusUpdateException) {
                log.error("Failed to update order status after processing failure for file: {} (order ID: {}). Error: {}",
                        originalFileName, orderId, statusUpdateException.getMessage(), statusUpdateException);
            }
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR , "PDF 처리 실패: " + originalFileName);
        } finally {
            if (originTempFile != null && originTempFile.exists()) {
                if (!originTempFile.delete()) {
                    log.warn("Failed to delete origin temp file: {}", originTempFile.getName());
                }
            }
        }
    }

    @Async("ioBoundTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void uploadAndPersistAsync(Long pdfId, Long documentId, File watermarkedFile, String storedFileName, String originalFileName,
                                      Long orderId, String logName) {
        try {
            Document document = documentRepository.findByIdOrElseThrow(documentId);
            String pdfUrl = s3Util.uploadPdfAndGetSignedUrlForAdmin(
                    document,
                    watermarkedFile,
                    storedFileName,
                    originalFileName,
                    new S3UploadProgressListener(eventPublisher, pdfId)
            );
            log.debug("S3 upload completed for file: {}", logName);

            // 사전 생성 Pdf 업데이트
            Pdf pdf = pdfRepository.findByIdOrElseThrow(pdfId);
            pdf.updatePdfUrlForAdmin(pdfUrl);
            pdf.updateUploadStatus(EPdfUploadStatus.COMPLETED);
            pdfRepository.save(pdf);

            orderStatusUpdateService.execute(orderId);
            log.info("Successfully completed PDF processing for file: {} (document ID: {})", logName, documentId);
        } catch (Exception ex) {
            log.error("Failed upload/persist for file: {} (document ID: {}). Error: {}", logName, documentId, ex.getMessage(), ex);
            try {
                Pdf target = pdfRepository.findByIdOrElseThrow(pdfId);
                target.updateUploadStatus(EPdfUploadStatus.FAILED);
                pdfRepository.save(target);
                PdfProgressEvent event = PdfProgressEvent.createCustomEvent(pdfId, "pdf_upload_status", EPdfUploadStatus.FAILED);
                eventPublisher.publishEvent(event);
            } catch (Exception ignored) {
                log.error("Failed to update PDF status to FAILED for file: {} (pdf ID: {}). Error: {}", logName, pdfId, ignored.getMessage(), ignored);
            }
            throw ex;
        } finally {
            if (watermarkedFile != null && watermarkedFile.exists()) {
                if (!watermarkedFile.delete()) {
                    log.warn("Failed to delete watermarked temp file: {}", watermarkedFile.getName());
                }
            }
        }
    }


    private static class TempFileMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final File file;

        public TempFileMultipartFile(String name, String originalFilename, String contentType, File file) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.file = file;
        }

        @Override
        public String getName() { return name; }

        @Override
        public String getOriginalFilename() { return originalFilename; }

        @Override
        public String getContentType() { return contentType; }

        @Override
        public boolean isEmpty() {
            return file.length() == 0;
        }

        @Override
        public long getSize() {
            return file.length();
        }

        @Override
        public byte[] getBytes() throws IOException {
            return Files.readAllBytes(file.toPath());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

