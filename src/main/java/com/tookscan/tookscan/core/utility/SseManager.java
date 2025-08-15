package com.tookscan.tookscan.core.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.config.SseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 통합 SSE 관리자 클래스
 * - SseEmitter 생성 및 생명주기 관리
 * - 구독자 등록/해제 (Generic 타입 지원)
 * - 실제 SSE 데이터 전송 (단일/브로드캐스트)
 * - 연결 메타데이터 관리
 * - JSON 변환 및 에러 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseManager {

    private final ObjectMapper objectMapper;

    // Generic 타입별 구독자 관리 (예: Long pdfId, String userId 등)
    private final Map<String, Map<Object, Set<SseEmitter>>> subscribersByType = new ConcurrentHashMap<>();
    
    // 연결 ID 생성기
    private final AtomicLong connectionIdGenerator = new AtomicLong(0);
    
    // 연결 메타데이터 (emitter -> 연결 정보)
    private final Map<SseEmitter, ConnectionInfo> connectionMetadata = new ConcurrentHashMap<>();

    /**
     * SSE 연결 생성 (순수 생성 및 관리)
     * @param subscriptionType 구독 타입 (예: "pdf", "order", "user")
     * @param subscriptionId 구독 ID (예: pdfId, orderId, userId)
     * @return 생성된 SseEmitter
     */
    public SseEmitter createSubscription(String subscriptionType, Object subscriptionId) {
        SseEmitter emitter = SseConfig.createSseEmitter();
        String connectionId = generateConnectionId();
        
        // 연결 메타데이터 등록
        ConnectionInfo connectionInfo = new ConnectionInfo(connectionId, subscriptionType, subscriptionId, System.currentTimeMillis());
        connectionMetadata.put(emitter, connectionInfo);
        
        // 구독자 목록에 추가
        subscribersByType
            .computeIfAbsent(subscriptionType, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(subscriptionId, k -> new CopyOnWriteArraySet<>())
            .add(emitter);
        
        // 콜백 설정
        SseConfig.setupSseEmitterCallbacks(
            emitter,
            () -> removeSubscription(subscriptionType, subscriptionId, emitter, "completion"),
            () -> removeSubscription(subscriptionType, subscriptionId, emitter, "timeout"),
            () -> removeSubscription(subscriptionType, subscriptionId, emitter, "error")
        );
        
        // 초기화 이벤트 발송
        sendInitEvent(emitter, connectionId);
        
        log.atInfo()
            .addKeyValue("subscription_type", subscriptionType)
            .addKeyValue("subscription_id", subscriptionId)
            .addKeyValue("connection_id", connectionId)
            .addKeyValue("current_subscribers", getSubscriberCount(subscriptionType, subscriptionId))
            .log("[SSE Manager] Subscription created");
            
        return emitter;
    }

    /**
     * 특정 구독의 모든 구독자 반환
     */
    public Set<SseEmitter> getSubscribers(String subscriptionType, Object subscriptionId) {
        Map<Object, Set<SseEmitter>> typeSubscribers = subscribersByType.get(subscriptionType);
        if (typeSubscribers == null) {
            return Set.of();
        }
        
        Set<SseEmitter> subscribers = typeSubscribers.get(subscriptionId);
        return subscribers != null ? Set.copyOf(subscribers) : Set.of();
    }

    /**
     * 특정 구독의 구독자 수 반환
     */
    public int getSubscriberCount(String subscriptionType, Object subscriptionId) {
        Map<Object, Set<SseEmitter>> typeSubscribers = subscribersByType.get(subscriptionType);
        if (typeSubscribers == null) {
            return 0;
        }
        
        Set<SseEmitter> subscribers = typeSubscribers.get(subscriptionId);
        return subscribers != null ? subscribers.size() : 0;
    }

    /**
     * 구독 해제 (연결 종료)
     */
    public void removeSubscription(String subscriptionType, Object subscriptionId, SseEmitter emitter, String reason) {
        Map<Object, Set<SseEmitter>> typeSubscribers = subscribersByType.get(subscriptionType);
        if (typeSubscribers != null) {
            Set<SseEmitter> subscribers = typeSubscribers.get(subscriptionId);
            if (subscribers != null) {
                subscribers.remove(emitter);
                
                // 구독자가 없으면 맵에서 제거
                if (subscribers.isEmpty()) {
                    typeSubscribers.remove(subscriptionId);
                    
                    // 타입별 구독자도 없으면 제거
                    if (typeSubscribers.isEmpty()) {
                        subscribersByType.remove(subscriptionType);
                    }
                }
            }
        }
        
        // 메타데이터 정리
        ConnectionInfo connectionInfo = connectionMetadata.remove(emitter);
        
        if (connectionInfo != null) {
            log.atInfo()
                .addKeyValue("subscription_type", subscriptionType)
                .addKeyValue("subscription_id", subscriptionId)
                .addKeyValue("connection_id", connectionInfo.connectionId)
                .addKeyValue("reason", reason)
                .addKeyValue("connection_duration_ms", System.currentTimeMillis() - connectionInfo.createdAt)
                .addKeyValue("remaining_subscribers", getSubscriberCount(subscriptionType, subscriptionId))
                .log("[SSE Manager] Subscription removed");
        }
    }

    /**
     * 모든 구독 정보 반환 (모니터링용)
     */
    public Map<String, Map<Object, Integer>> getAllSubscriptionStats() {
        Map<String, Map<Object, Integer>> stats = new ConcurrentHashMap<>();
        
        subscribersByType.forEach((type, typeSubscribers) -> {
            Map<Object, Integer> typeStats = new ConcurrentHashMap<>();
            typeSubscribers.forEach((id, subscribers) -> typeStats.put(id, subscribers.size()));
            stats.put(type, typeStats);
        });
        
        return stats;
    }

    /**
     * SSE 초기화 이벤트 발송
     */
    public void sendInitEvent(SseEmitter emitter, String connectionId) {
        try {
            emitter.send(SseEmitter.event()
                .name("INIT")
                .data("connected")
                .id(connectionId));
                
            log.atDebug()
                .addKeyValue("connection_id", connectionId)
                .log("[SSE Manager] Init event sent successfully");
                
        } catch (IOException e) {
            log.atWarn()
                .setCause(e)
                .addKeyValue("connection_id", connectionId)
                .log("[SSE Manager] Failed to send init event");
        }
    }

    /**
     * 단일 SSE 이벤트 발송
     */
    public boolean sendSseEvent(SseEmitter emitter, String eventId, String eventName, Object data) {
        try {
            String dataJson = convertToJson(data);
            
            emitter.send(SseEmitter.event()
                .id(eventId)
                .name(eventName)
                .data(dataJson));
            
            log.atDebug()
                .addKeyValue("event_id", eventId)
                .addKeyValue("event_name", eventName)
                .log("[SSE Manager] SSE event sent successfully");
                
            return true;
            
        } catch (IOException e) {
            log.atWarn()
                .setCause(e)
                .addKeyValue("event_id", eventId)
                .addKeyValue("event_name", eventName)
                .log("[SSE Manager] Failed to send SSE event");
                
            return false;
        }
    }

    /**
     * 여러 구독자에게 동시에 SSE 이벤트 발송 (비동기)
     * @param emitters 대상 emitter 집합
     * @param eventId 이벤트 ID
     * @param eventName 이벤트 이름
     * @param data 이벤트 데이터
     * @return 전송 실패한 emitter들의 집합
     */
    public CompletableFuture<Set<SseEmitter>> broadcastSseEvent(Set<SseEmitter> emitters, String eventId, String eventName, Object data) {
        if (emitters == null || emitters.isEmpty()) {
            return CompletableFuture.completedFuture(Set.of());
        }

        return CompletableFuture.supplyAsync(() -> {
            String dataJson = convertToJson(data);
            Set<SseEmitter> failedEmitters = new ConcurrentHashMap<SseEmitter, Boolean>().keySet();
            
            emitters.parallelStream().forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name(eventName)
                        .data(dataJson));
                        
                } catch (IOException e) {
                    failedEmitters.add(emitter);
                    log.atDebug()
                        .setCause(e)
                        .addKeyValue("event_id", eventId)
                        .addKeyValue("event_name", eventName)
                        .log("[SSE Manager] Failed to send SSE event to emitter");
                }
            });
            
            if (!failedEmitters.isEmpty()) {
                log.atInfo()
                    .addKeyValue("failed_count", failedEmitters.size())
                    .addKeyValue("total_count", emitters.size())
                    .addKeyValue("event_id", eventId)
                    .addKeyValue("event_name", eventName)
                    .log("[SSE Manager] SSE broadcast completed with some failures");
            }
            
            return failedEmitters;
        });
    }

    /**
     * 특정 구독의 모든 구독자에게 브로드캐스트
     */
    public CompletableFuture<Set<SseEmitter>> broadcastToSubscription(String subscriptionType, Object subscriptionId, String eventId, String eventName, Object data) {
        Set<SseEmitter> subscribers = getSubscribers(subscriptionType, subscriptionId);
        return broadcastSseEvent(subscribers, eventId, eventName, data);
    }

    /**
     * 모든 SSE 연결을 완료 처리 (서버 종료 시 호출)
     */
    public void completeAllEmitters() {
        subscribersByType.forEach((type, idMap) -> {
            idMap.forEach((id, emitters) -> {
                // 스냅샷으로 안전하게 순회
                Set<SseEmitter> snapshot = Set.copyOf(emitters);
                snapshot.forEach(emitter -> {
                    try {
                        emitter.complete();
                    } catch (Exception ignore) {
                    }
                });
            });
        });

        log.atInfo().log("[SSE Manager] Completed all emitters for graceful shutdown");
    }

    /**
     * 객체를 JSON 문자열로 변환
     */
    private String convertToJson(Object obj) {
        if (obj == null) {
            return "";
        }
        
        if (obj instanceof String) {
            return (String) obj;
        }
        
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.atWarn()
                .setCause(e)
                .addKeyValue("object_type", obj.getClass().getSimpleName())
                .log("[SSE Manager] Failed to convert object to JSON, using toString()");
            return String.valueOf(obj);
        }
    }

    /**
     * 연결 ID 생성
     */
    private String generateConnectionId() {
        return "sse-" + System.currentTimeMillis() + "-" + connectionIdGenerator.incrementAndGet();
    }

    /**
     * 연결 정보를 담는 내부 클래스
     */
    private static class ConnectionInfo {
        final String connectionId;
        final long createdAt;

        ConnectionInfo(String connectionId, String subscriptionType, Object subscriptionId, long createdAt) {
            this.connectionId = connectionId;
            this.createdAt = createdAt;
        }
    }
}