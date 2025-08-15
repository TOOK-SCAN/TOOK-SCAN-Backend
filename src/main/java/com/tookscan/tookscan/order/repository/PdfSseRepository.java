package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.event.PdfSseEvent;
import java.util.List;

public interface PdfSseRepository {
    long nextEventId(Long pdfId);
    void appendEvent(Long pdfId, PdfSseEvent event, int maxHistory, long ttlSeconds);
    List<PdfSseEvent> readAll(Long pdfId);
    List<PdfSseEvent> readAfter(Long pdfId, long lastEventId);
    void publish(Long pdfId, PdfSseEvent event);
}


