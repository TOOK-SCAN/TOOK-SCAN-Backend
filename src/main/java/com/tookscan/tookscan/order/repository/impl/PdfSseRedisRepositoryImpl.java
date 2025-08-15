package com.tookscan.tookscan.order.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tookscan.tookscan.core.dto.PdfSsePubSubDto;
import com.tookscan.tookscan.order.domain.redis.PdfSseHistory;
import com.tookscan.tookscan.order.repository.redis.PdfSseRedisRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PdfSseRedisRepositoryImpl implements PdfSseRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_SEQ = "order:pdf:sse:%d:seq";
    private static final String KEY_HISTORY = "order:pdf:sse:%d:history";
    public static final String CHANNEL = "order:pdf:sse:channel";

    @Override
    public long nextEventId(Long pdfId) {
        String key = keySeq(pdfId);
        Long id = stringRedisTemplate.opsForValue().increment(key);
        return id == null ? 1L : id;
    }

    @Override
    public void appendEvent(Long pdfId, PdfSseHistory event, int maxHistory, long ttlSeconds) {
        String key = keyHistory(pdfId);
        String payload = toJson(event);
        stringRedisTemplate.opsForList().rightPush(key, payload);
        stringRedisTemplate.opsForList().trim(key, Math.max(-maxHistory, -1 * (long) maxHistory), -1);
        if (ttlSeconds > 0) {
            stringRedisTemplate.expire(key, java.time.Duration.ofSeconds(ttlSeconds));
        }
    }

    @Override
    public List<PdfSseHistory> readAll(Long pdfId) {
        String key = keyHistory(pdfId);
        List<String> list = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (list == null) return new ArrayList<>();
        return list.stream().map(this::fromJson).collect(Collectors.toList());
    }

    @Override
    public List<PdfSseHistory> readAfter(Long pdfId, long lastEventId) {
        return readAll(pdfId).stream()
                .filter(e -> e.getId() > lastEventId)
                .collect(Collectors.toList());
    }

    @Override
    public void publish(Long pdfId, PdfSseHistory event) {
        PdfSsePubSubDto pubSubDto = PdfSsePubSubDto.create(pdfId, event);
        String wrapper = toJson(pubSubDto);
        stringRedisTemplate.convertAndSend(CHANNEL, wrapper);
    }

    private String keySeq(Long pdfId) {
        return String.format(KEY_SEQ, pdfId);
    }

    private String keyHistory(Long pdfId) {
        return String.format(KEY_HISTORY, pdfId);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private PdfSseHistory fromJson(String json) {
        try {
            return objectMapper.readValue(json, PdfSseHistory.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}


