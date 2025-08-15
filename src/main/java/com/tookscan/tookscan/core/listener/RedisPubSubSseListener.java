package com.tookscan.tookscan.core.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.dto.PdfSsePubSubDto;
import com.tookscan.tookscan.core.utility.SseManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

/**
 * Redis PubSub SSE 메시지 리스너
 * - 프로젝트 패턴 준수: core/listener 폴더의 @Component 리스너
 * - Redis PubSub으로 수신된 SSE 이벤트를 처리
 * - 다른 인스턴스에서 발생한 이벤트를 로컬 구독자들에게 전달
 * - Spring Bean으로 관리되어 의존성 주입 최적화
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPubSubSseListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseManager sseManager;

    /**
     * Redis PubSub 메시지 수신 처리
     * - PdfSsePubSubDto 역직렬화
     * - PdfProgressEvent 재발행으로 SseListener에게 위임
     * 
     * @param message Redis PubSub 메시지
     * @param pattern 구독 패턴 (사용하지 않음)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            PdfSsePubSubDto pubSubDto = objectMapper.readValue(messageBody, PdfSsePubSubDto.class);

            // 같은 인스턴스에서 발행한 메시지는 무시 (중복 브로드캐스트 방지)
            String currentInstance = getCurrentInstanceId();
            if (pubSubDto.getPublisherInstance() != null && pubSubDto.getPublisherInstance().equals(currentInstance)) {
                log.atDebug()
                    .addKeyValue("pdf_id", pubSubDto.getPdfId())
                    .addKeyValue("event_id", pubSubDto.getEvent().getId())
                    .addKeyValue("publisher_instance", pubSubDto.getPublisherInstance())
                    .log("[Redis PubSub SSE Listener] Ignored self-published message");
                return;
            }

            log.atDebug()
                .addKeyValue("pdf_id", pubSubDto.getPdfId())
                .addKeyValue("event_id", pubSubDto.getEvent().getId())
                .addKeyValue("event_name", pubSubDto.getEvent().getName())
                .addKeyValue("publisher_instance", pubSubDto.getPublisherInstance())
                .addKeyValue("message_age_ms", calculateMessageAge(pubSubDto))
                .log("[Redis PubSub SSE Listener] Received SSE PubSub message");

            // 로컬 구독자에게만 브로드캐스트 (재발행 루프 방지)
            sseManager.broadcastToSubscription(
                "pdf",
                pubSubDto.getPdfId(),
                String.valueOf(pubSubDto.getEvent().getId()),
                pubSubDto.getEvent().getName(),
                pubSubDto.getEvent().getDataJson()
            );

            log.atDebug()
                .addKeyValue("pdf_id", pubSubDto.getPdfId())
                .addKeyValue("event_id", pubSubDto.getEvent().getId())
                .addKeyValue("event_name", pubSubDto.getEvent().getName())
                .log("[Redis PubSub SSE Listener] SSE PubSub message processed and event republished");

        } catch (JsonProcessingException e) {
            log.atWarn()
                .setCause(e)
                .addKeyValue("message_body", new String(message.getBody()))
                .addKeyValue("message_length", message.getBody().length)
                .log("[Redis PubSub SSE Listener] Failed to deserialize PubSub message");

        } catch (Exception e) {
            log.atError()
                .setCause(e)
                .addKeyValue("message_body", new String(message.getBody()))
                .log("[Redis PubSub SSE Listener] Unexpected error handling PubSub message");
        }
    }

    private String getCurrentInstanceId() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return hostname + "-" + pid;
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * PubSub 메시지의 지연 시간 계산
     * - 메시지 생성 시각과 현재 시각의 차이
     * - null 체크 포함
     */
    private Long calculateMessageAge(PdfSsePubSubDto pubSubDto) {
        if (pubSubDto.getCreatedAt() == null) {
            return null;
        }
        return System.currentTimeMillis() - pubSubDto.getCreatedAt();
    }

    /**
     * 리스너 상태 정보 반환 (모니터링용)
     */
    public String getListenerInfo() {
        return "RedisPubSubSseListener{" +
            "objectMapper=" + objectMapper.getClass().getSimpleName() +
            ", sseManager=" + sseManager.getClass().getSimpleName() +
            '}';
    }
}