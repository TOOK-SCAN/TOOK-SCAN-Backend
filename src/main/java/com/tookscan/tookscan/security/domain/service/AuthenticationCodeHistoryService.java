package com.tookscan.tookscan.security.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCodeHistory;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCodeHistoryService {

    private static final Integer MAX_ISSUING_AUTHENTICATION_CODE = 5;

    public AuthenticationCodeHistory createAuthenticationCodeHistory(String phoneNumber) {
        return AuthenticationCodeHistory.builder()
                .phoneNumber(phoneNumber)
                .count(1)
                .build();
    }

    public void validateAuthenticationCodeHistory(AuthenticationCodeHistory history) {
        if (isBlockedIssuingAuthenticationCode(history)) {
            throw new CommonException(ErrorCode.TOO_MANY_AUTHENTICATION_CODE_REQUESTS);
        }
        if (isTooFastIssuingAuthenticationCode(history)) {
            throw new CommonException(ErrorCode.TOO_FAST_AUTHENTICATION_CODE_REQUESTS);
        }
    }

    public AuthenticationCodeHistory incrementAuthenticationCodeCount(AuthenticationCodeHistory history) {
        history.incrementCount();
        history.updateLastSentAt();
        return history;
    }

    private Boolean isBlockedIssuingAuthenticationCode(AuthenticationCodeHistory history) {
        if (history == null) {
            return false;
        }
        return history.getCount() >= MAX_ISSUING_AUTHENTICATION_CODE;
    }

    private Boolean isTooFastIssuingAuthenticationCode(AuthenticationCodeHistory history) {
        if (history == null) {
            return false;
        }
        return history.getLastSentAt().isAfter(LocalDateTime.now().minusSeconds(10));
    }
}
