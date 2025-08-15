package com.tookscan.tookscan.core.listener;

import com.tookscan.tookscan.core.utility.SseManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 종료 시 SSE 연결을 정리하여 타임아웃/예외를 방지
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseShutdownListener {

    private final SseManager sseManager;

    @PreDestroy
    public void onShutdown() {
        try {
            sseManager.completeAllEmitters();
            log.atInfo().log("[SSE Shutdown] Completed all SSE emitters before shutdown");
        } catch (Exception e) {
            log.atWarn().setCause(e).log("[SSE Shutdown] Failed to complete SSE emitters");
        }
    }
}


