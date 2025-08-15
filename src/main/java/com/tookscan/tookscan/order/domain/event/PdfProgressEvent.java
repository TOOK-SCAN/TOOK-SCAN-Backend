package com.tookscan.tookscan.order.domain.event;

import lombok.Builder;
import lombok.Getter;

/**
 * PDF 진행상황 이벤트 클래스
 * - SSE로 클라이언트에게 전송될 PDF 처리 진행상황
 * - 기존 패턴과 동일한 구조로 이벤트 정의
 */
@Getter
@Builder
public class PdfProgressEvent {

    /**
     * PDF ID (구독자 식별용)
     */
    private final Long pdfId;
    
    /**
     * 이벤트 이름 (예: "pdf_upload_progress", "pdf_upload_status")
     */
    private final String eventName;
    
    /**
     * 이벤트 데이터 (진행률, 상태값 등)
     */
    private final Object eventData;
    
    /**
     * 이벤트 발생 시각
     */
    private final long timestamp;

    /**
     * PDF 업로드 진행률 이벤트 생성
     */
    public static PdfProgressEvent createProgressEvent(Long pdfId, Object progressData) {
        return PdfProgressEvent.builder()
            .pdfId(pdfId)
            .eventName("pdf_upload_progress")
            .eventData(progressData)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    /**
     * PDF 업로드 상태 변경 이벤트 생성
     */
    public static PdfProgressEvent createStatusEvent(Long pdfId, Object statusData) {
        return PdfProgressEvent.builder()
            .pdfId(pdfId)
            .eventName("pdf_upload_status")
            .eventData(statusData)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    /**
     * 커스텀 이벤트 생성
     */
    public static PdfProgressEvent createCustomEvent(Long pdfId, String eventName, Object eventData) {
        return PdfProgressEvent.builder()
            .pdfId(pdfId)
            .eventName(eventName)
            .eventData(eventData)
            .timestamp(System.currentTimeMillis())
            .build();
    }

    @Override
    public String toString() {
        return "PdfProgressEvent{" +
            "pdfId=" + pdfId +
            ", eventName='" + eventName + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}