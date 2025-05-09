package com.tookscan.tookscan.mail.domain.redis;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "test_mail_history", timeToLive = 60 * 10) // 10분
public class TestMailHistory {
    @Id
    private String email;

    private Integer count;

    private LocalDateTime lastSentAt;

    @Builder
    public TestMailHistory(
            String email,
            Integer count
    ) {
        this.email = email;
        this.count = count;

        this.lastSentAt = LocalDateTime.now();
    }

    public void incrementCount() {
        this.count++;
    }

    public void updateLastSentAt() {
        this.lastSentAt = LocalDateTime.now();
    }
}
