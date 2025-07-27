package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.DeleteExpiredPdfsUseCase;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteExpiredPdfsService implements DeleteExpiredPdfsUseCase {

    private final PdfRepository pdfRepository;
    private final S3Util s3Util;

    @Override
    @Async("fileProcessingTaskExecutor")
    @Transactional
    public void execute() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        
        List<Pdf> expiredPdfs = pdfRepository.findPdfsByOrderPdfSendDateBefore(twoWeeksAgo);
        
        log.info("Found {} PDFs to expire and delete from S3 (pdfSendDate before {})", expiredPdfs.size(), twoWeeksAgo);
        
        int successCount = 0;
        int failureCount = 0;
        List<Pdf> updatedPdfs = new ArrayList<>();
        
        // S3 삭제 작업을 병렬로 처리
        expiredPdfs.parallelStream().forEach(pdf -> {
            try {
                // S3에서 파일 삭제 (병렬 처리)
                s3Util.deletePdfFromS3(pdf);
                
                // PDF 엔티티의 expiredAt 설정
                synchronized (updatedPdfs) {
                    pdf.updateExpiredAt(LocalDateTime.now());
                    updatedPdfs.add(pdf);
                }
                
                log.debug("Successfully expired PDF and deleted from S3: id={}, orderId={}, pdfSendDate={}", 
                    pdf.getId(), pdf.getDocument().getOrder().getId(), pdf.getDocument().getOrder().getPdfSendDate());
                
            } catch (Exception e) {
                log.error("Failed to expire PDF: id={}, orderId={}, error={}", 
                    pdf.getId(), pdf.getDocument().getOrder().getId(), e.getMessage(), e);
            }
        });
        
        successCount = updatedPdfs.size();
        failureCount = expiredPdfs.size() - successCount;
        
        // 성공적으로 처리된 PDF들을 일괄 저장
        if (!updatedPdfs.isEmpty()) {
            pdfRepository.saveAll(updatedPdfs);
            log.info("Updated {} PDFs with expiredAt timestamp", updatedPdfs.size());
        }
        
        log.info("PDF expiration completed - Success: {}, Failures: {}", successCount, failureCount);
    }
}