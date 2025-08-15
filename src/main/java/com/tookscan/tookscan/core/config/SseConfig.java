package com.tookscan.tookscan.core.config;

import com.tookscan.tookscan.core.listener.RedisPubSubSseListener;
import com.tookscan.tookscan.order.repository.impl.PdfSseRedisRepositoryImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 관련 설정을 통합 관리하는 Config 클래스
 * - Redis PubSub 설정
 * - SSE 기본 설정 상수
 * - Message Listener 등록
 */
/**
 * SSE 관련 설정을 통합 관리하는 Config 클래스 (단순화)
 * - Redis PubSub 설정만 담당
 * - SSE 기본 설정 상수 관리
 * - 리스너는 별도 컴포넌트로 분리하여 의존성 주입
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SseConfig {

    // SSE 설정 상수
    public static final long DEFAULT_TIMEOUT = 60L * 1000L; // 1분
    public static final int MAX_HISTORY = 200;
    public static final long HISTORY_TTL_SECONDS = 60L * 60L; // 1시간
    
    private final RedisConnectionFactory connectionFactory;
    private final RedisPubSubSseListener redisPubSubSseListener;

    /**
     * Redis PubSub Container 초기화
     * - SSE 이벤트를 클러스터 간 동기화하기 위한 설정
     * - RedisPubSubSseListener 컴포넌트를 의존성 주입으로 사용
     */
    @PostConstruct
    public void initializeRedisMessageListener() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
            new MessageListenerAdapter(redisPubSubSseListener), 
            new ChannelTopic(PdfSseRedisRepositoryImpl.CHANNEL)
        );
        container.afterPropertiesSet();
        container.start();
        
        log.atInfo()
            .addKeyValue("channel", PdfSseRedisRepositoryImpl.CHANNEL)
            .addKeyValue("listener", redisPubSubSseListener.getClass().getSimpleName())
            .log("[SSE Config] Redis PubSub message listener initialized with injected component");
    }

    /**
     * SSE Emitter 기본 설정을 생성하는 유틸리티 메서드
     */
    public static SseEmitter createSseEmitter() {
        return new SseEmitter(DEFAULT_TIMEOUT);
    }

    /**
     * SSE Emitter에 기본 콜백들을 설정하는 유틸리티 메서드
     */
    public static void setupSseEmitterCallbacks(SseEmitter emitter, Runnable onComplete, Runnable onTimeout, Runnable onError) {
        emitter.onCompletion(onComplete);
        emitter.onTimeout(onTimeout);
        emitter.onError((e) -> onError.run());
    }
}