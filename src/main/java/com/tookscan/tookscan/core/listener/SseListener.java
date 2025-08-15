package com.tookscan.tookscan.core.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 범용 SSE 이벤트 리스너
 * - 트랜잭션 커밋 후 발생한 도메인 이벤트들을 수신
 * - 등록된 도메인별 핸들러에게 위임
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseListener {

    private final List<SseDomainEventHandler> handlers;

    @Async("sseTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDomainEventAfterCommit(Object event) {
        delegateToHandlers(event);
    }

    @Async("sseTaskExecutor")
    @EventListener
    public void onDomainEventNoTx(Object event) {
        delegateToHandlers(event);
    }

    private void delegateToHandlers(Object event) {
        try {
            handlers.stream()
                .filter(h -> safeCanHandle(h, event))
                .forEach(h -> safeHandle(h, event));
        } catch (Exception ex) {
            log.atError()
                .setCause(ex)
                .addKeyValue("event_type", event != null ? event.getClass().getSimpleName() : null)
                .log("[SSE Listener] Unexpected error while delegating domain event");
        }
    }

    private boolean safeCanHandle(SseDomainEventHandler handler, Object event) {
        try {
            return handler.canHandle(event);
        } catch (Exception ex) {
            log.atWarn()
                .setCause(ex)
                .addKeyValue("handler", handler.getClass().getSimpleName())
                .log("[SSE Listener] Handler.canHandle threw exception");
            return false;
        }
    }

    private void safeHandle(SseDomainEventHandler handler, Object event) {
        try {
            handler.handle(event);
        } catch (Exception ex) {
            log.atError()
                .setCause(ex)
                .addKeyValue("handler", handler.getClass().getSimpleName())
                .addKeyValue("event_type", event != null ? event.getClass().getSimpleName() : null)
                .log("[SSE Listener] Handler.handle failed");
        }
    }
}