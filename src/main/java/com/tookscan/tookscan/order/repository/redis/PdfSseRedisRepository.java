package com.tookscan.tookscan.order.repository.redis;

import com.tookscan.tookscan.order.domain.redis.PdfSseHistory;
import java.util.List;

public interface PdfSseRedisRepository {
    long nextEventId(Long pdfId);
    void appendEvent(Long pdfId, PdfSseHistory event, int maxHistory, long ttlSeconds);
    List<PdfSseHistory> readAll(Long pdfId);
    List<PdfSseHistory> readAfter(Long pdfId, long lastEventId);
    void publish(Long pdfId, PdfSseHistory event);
}


