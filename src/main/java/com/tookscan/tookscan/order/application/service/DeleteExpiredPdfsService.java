package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.DeleteExpiredPdfsUseCase;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteExpiredPdfsService implements DeleteExpiredPdfsUseCase {

    private final PdfRepository pdfRepository;
    private final S3Util s3Util;

    @Override
    @Async("fileProcessingTaskExecutor")
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "delete expired pdfs",
        userType = "System"
    )
    public void execute() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        
        List<Pdf> expiredPdfs = pdfRepository.findPdfsByOrderPdfSendDateBefore(twoWeeksAgo);
        
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
            } catch (Exception e) {
                // do nothing
            }
        });
        
        int successCount = updatedPdfs.size();
        int failureCount = expiredPdfs.size() - successCount;
        
        // 성공적으로 처리된 PDF들을 일괄 저장
        if (!updatedPdfs.isEmpty()) {
            pdfRepository.saveAll(updatedPdfs);
        }
        
        LogContext.put("success_count", successCount);
        LogContext.put("failure_count", failureCount);
    }
}
