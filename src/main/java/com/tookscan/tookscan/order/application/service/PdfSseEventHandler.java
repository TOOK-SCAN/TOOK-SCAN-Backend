package com.tookscan.tookscan.order.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.listener.SseDomainEventHandler;
import com.tookscan.tookscan.core.utility.SseManager;
import com.tookscan.tookscan.order.domain.event.PdfProgressEvent;
import com.tookscan.tookscan.order.domain.event.PdfSseEvent;
import com.tookscan.tookscan.order.repository.PdfSseRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.tookscan.tookscan.core.config.SseConfig.HISTORY_TTL_SECONDS;
import static com.tookscan.tookscan.core.config.SseConfig.MAX_HISTORY;

/**
 * PDF 도메인 SSE 이벤트 핸들러
 * - 이벤트 저장/발행/로컬 브로드캐스트 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfSseEventHandler implements SseDomainEventHandler {

    private final PdfSseRepository pdfSseRepository;
    private final SseManager sseManager;
    private final ObjectMapper objectMapper;

    @Override
    public boolean canHandle(Object event) {
        return event instanceof PdfProgressEvent;
    }

    @Override
    public void handle(Object event) {
        PdfProgressEvent pdfEvent = (PdfProgressEvent) event;

        long eventId = pdfSseRepository.nextEventId(pdfEvent.getPdfId());
        String dataJson = convertEventDataToJson(pdfEvent.getEventData());

        PdfSseEvent sseEvent = PdfSseEvent.builder()
            .id(eventId)
            .name(pdfEvent.getEventName())
            .dataJson(dataJson)
            .timestamp(pdfEvent.getTimestamp())
            .build();

        // 1) Redis 리스트 저장 + TTL/사이즈 관리
        pdfSseRepository.appendEvent(pdfEvent.getPdfId(), sseEvent, MAX_HISTORY, HISTORY_TTL_SECONDS);
        // 2) Redis PubSub 발행 (다른 인스턴스 브릿지용)
        pdfSseRepository.publish(pdfEvent.getPdfId(), sseEvent);
        // 3) 로컬 구독자 브로드캐스트
        sendToLocalSubscribers(pdfEvent.getPdfId(), String.valueOf(eventId), pdfEvent.getEventName(), dataJson);

        log.atInfo()
            .addKeyValue("pdf_id", pdfEvent.getPdfId())
            .addKeyValue("event_id", eventId)
            .addKeyValue("event_name", pdfEvent.getEventName())
            .addKeyValue("subscriber_count", sseManager.getSubscriberCount("pdf", pdfEvent.getPdfId()))
            .log("[PDF SSE Handler] Event processed and broadcasted");
    }

    private void sendToLocalSubscribers(Long pdfId, String eventId, String eventName, String dataJson) {
        Set<SseEmitter> subscribers = sseManager.getSubscribers("pdf", pdfId);
        if (subscribers.isEmpty()) {
            return;
        }
        sseManager.broadcastSseEvent(subscribers, eventId, eventName, dataJson)
            .thenAccept(failedEmitters -> {
                if (!failedEmitters.isEmpty()) {
                    failedEmitters.forEach(emitter -> sseManager.removeSubscription("pdf", pdfId, emitter, "send_failure"));
                }
            })
            .exceptionally(throwable -> null);
    }

    private String convertEventDataToJson(Object eventData) {
        if (eventData == null) return "";
        if (eventData instanceof String) return (String) eventData;
        try {
            return objectMapper.writeValueAsString(eventData);
        } catch (Exception e) {
            return String.valueOf(eventData);
        }
    }
}


