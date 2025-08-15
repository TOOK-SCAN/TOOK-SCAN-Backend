package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.SseManager;
import com.tookscan.tookscan.order.application.usecase.SubscribePdfProgressUseCase;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.event.PdfSseEvent;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import com.tookscan.tookscan.order.repository.PdfRepository;
import com.tookscan.tookscan.order.repository.PdfSseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * PDF 진행상황 구독 서비스 - PDF 존재 검증 - 히스토리 재생(직접 수행) - 연결 관리는 SseManager 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribePdfProgressService implements SubscribePdfProgressUseCase {

    private final PdfRepository pdfRepository;
    private final PdfSseRepository pdfSseRepository;
    private final SseManager sseManager;

    @Override
    public SseEmitter execute(Long pdfId, String lastEventId) {
        Pdf pdf = pdfRepository.findByIdOrElseThrow(pdfId);

        if (pdf.getUploadStatus() == EPdfUploadStatus.COMPLETED) {
            throw new CommonException(ErrorCode.PDF_UPLOAD_ALREADY_COMPLETED);
        }
        if (pdf.getUploadStatus() == EPdfUploadStatus.FAILED) {
            throw new CommonException(ErrorCode.PDF_UPLOAD_ALREADY_FAILED);
        }

        SseEmitter emitter = sseManager.createSubscription("pdf", pdfId);

        // 히스토리 재생 (Last-Event-ID가 있는 경우에만)
        if (lastEventId != null && !lastEventId.isBlank()) {
            try {
                long last = Long.parseLong(lastEventId);
                List<PdfSseEvent> histories = pdfSseRepository.readAfter(pdfId, last);
                for (PdfSseEvent h : histories) {
                    sseManager.sendSseEvent(emitter, String.valueOf(h.getId()), h.getName(), h.getDataJson());
                }
                log.atInfo()
                        .addKeyValue("pdf_id", pdfId)
                        .addKeyValue("last_event_id", lastEventId)
                        .addKeyValue("replayed", histories.size())
                        .log("[PDF Progress Service] Replayed SSE history events");
            } catch (NumberFormatException e) {
                log.atWarn()
                        .addKeyValue("pdf_id", pdfId)
                        .addKeyValue("invalid_last_event_id", lastEventId)
                        .log("[PDF Progress Service] Invalid Last-Event-ID, skip replay");
            } catch (Exception e) {
                log.atError()
                        .setCause(e)
                        .addKeyValue("pdf_id", pdfId)
                        .log("[PDF Progress Service] Failed to replay SSE history");
            }
        }

        log.atInfo()
                .addKeyValue("pdf_id", pdfId)
                .addKeyValue("has_last_event_id", lastEventId != null)
                .addKeyValue("subscriber_count", sseManager.getSubscriberCount("pdf", pdfId))
                .log("[PDF Progress Service] PDF SSE subscription created");

        return emitter;
    }

    public int getPdfSubscriberCount(Long pdfId) {
        return sseManager.getSubscriberCount("pdf", pdfId);
    }
}
