package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.SubscribePdfProgressUseCase;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribePdfProgressService implements SubscribePdfProgressUseCase {

    private static final long DEFAULT_TIMEOUT = 60L * 60L * 1000L; // 1시간

    private final PdfRepository pdfRepository;

    // key: pdfId
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    @Override
    public SseEmitter execute(Long pdfId) {

        pdfRepository.findByIdOrElseThrow(pdfId);

        String key = buildKey(pdfId);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onCompletion(() -> emitterMap.remove(key));
        emitter.onTimeout(() -> emitterMap.remove(key));
        emitter.onError((e) -> emitterMap.remove(key));

        emitterMap.put(key, emitter);

        try {
            emitter.send(SseEmitter.event().name("INIT").data("connected"));
        } catch (IOException e) {
            log.warn("SSE init send failed: {}", e.getMessage());
        }

        return emitter;
    }

    public void sendEvent(Long pdfId, String name, Object data) {
        String key = buildKey(pdfId);
        SseEmitter emitter = emitterMap.get(key);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name(name).data(data));
        } catch (IOException e) {
            emitterMap.remove(key);
            emitter.completeWithError(e);
        }
    }

    private String buildKey(Long pdfId) {
        return String.valueOf(pdfId);
    }
}

