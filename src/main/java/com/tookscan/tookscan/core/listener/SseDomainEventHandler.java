package com.tookscan.tookscan.core.listener;

/**
 * 범용 SSE 도메인 이벤트 핸들러 인터페이스
 * - 도메인 서비스가 구현하여 SSE 전송을 수행
 */
public interface SseDomainEventHandler {

    boolean canHandle(Object event);

    void handle(Object event);
}


