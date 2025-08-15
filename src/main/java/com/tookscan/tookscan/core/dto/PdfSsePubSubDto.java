package com.tookscan.tookscan.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.order.domain.redis.PdfSseHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PDF SSE Redis PubSub 메시지 DTO
 * - Redis PubSub을 통해 클러스터 간 SSE 이벤트 동기화시 사용
 * - Jackson 직렬화/역직렬화 최적화
 * - 프로젝트 DTO 패턴 준수
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfSsePubSubDto {

    /**
     * PDF ID (구독자 식별용)
     */
    @JsonProperty("pdf_id")
    private Long pdfId;

    /**
     * SSE 이벤트 히스토리 정보
     */
    @JsonProperty("event")
    private PdfSseHistory event;

    /**
     * 메시지 생성 시각 (디버깅용)
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * 발행한 인스턴스 정보 (디버깅용)
     */
    @JsonProperty("publisher_instance")
    private String publisherInstance;

    /**
     * PubSub 메시지 생성 팩토리 메서드
     */
    public static PdfSsePubSubDto create(Long pdfId, PdfSseHistory event) {
        return PdfSsePubSubDto.builder()
            .pdfId(pdfId)
            .event(event)
            .createdAt(System.currentTimeMillis())
            .publisherInstance(getInstanceId())
            .build();
    }

    /**
     * PubSub 메시지 생성 (인스턴스 정보 없이)
     */
    public static PdfSsePubSubDto createSimple(Long pdfId, PdfSseHistory event) {
        return PdfSsePubSubDto.builder()
            .pdfId(pdfId)
            .event(event)
            .build();
    }

    /**
     * 현재 인스턴스 ID 생성 (호스트명 + 프로세스ID 조합)
     */
    private static String getInstanceId() {
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return hostname + "-" + pid;
        } catch (Exception e) {
            return "unknown-" + System.currentTimeMillis();
        }
    }

    @Override
    public String toString() {
        return "PdfSsePubSubDto{" +
            "pdfId=" + pdfId +
            ", eventId=" + (event != null ? event.getId() : null) +
            ", eventName='" + (event != null ? event.getName() : null) + '\'' +
            ", createdAt=" + createdAt +
            ", publisherInstance='" + publisherInstance + '\'' +
            '}';
    }
}