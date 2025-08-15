package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.order.domain.event.PdfSseEvent;
import com.tookscan.tookscan.order.domain.redis.PdfSseHistory;
import com.tookscan.tookscan.order.repository.PdfSseRepository;
import com.tookscan.tookscan.order.repository.redis.PdfSseRedisRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PdfSseRepositoryImpl implements PdfSseRepository {

	private final PdfSseRedisRepository redis;

	@Override
	public long nextEventId(Long pdfId) {
		return redis.nextEventId(pdfId);
	}

	@Override
	public void appendEvent(Long pdfId, PdfSseEvent event, int maxHistory, long ttlSeconds) {
		redis.appendEvent(pdfId, toHistory(event), maxHistory, ttlSeconds);
	}

	@Override
	public List<PdfSseEvent> readAll(Long pdfId) {
		return redis.readAll(pdfId).stream().map(this::toEvent).collect(Collectors.toList());
	}

	@Override
	public List<PdfSseEvent> readAfter(Long pdfId, long lastEventId) {
		return redis.readAfter(pdfId, lastEventId).stream().map(this::toEvent).collect(Collectors.toList());
	}

	@Override
	public void publish(Long pdfId, PdfSseEvent event) {
		redis.publish(pdfId, toHistory(event));
	}

	private PdfSseHistory toHistory(PdfSseEvent e) {
		return PdfSseHistory.builder()
			.id(e.getId())
			.name(e.getName())
			.dataJson(e.getDataJson())
			.timestamp(e.getTimestamp())
			.build();
	}

	private PdfSseEvent toEvent(PdfSseHistory h) {
		return PdfSseEvent.builder()
			.id(h.getId())
			.name(h.getName())
			.dataJson(h.getDataJson())
			.timestamp(h.getTimestamp())
			.build();
	}
}


