package com.tookscan.tookscan.security.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCodeService {

    // 인증 코드 생성
    public AuthenticationCode createAuthenticationCode(String phoneNumber, String code) {
        return AuthenticationCode.builder()
                .phoneNumber(phoneNumber)
                .value(code)
                .build();
    }

    // 인증 코드 검증이 완료되었는지 확인
    public void validateAuthenticationCode(AuthenticationCode authenticationCode) {

        if (authenticationCode == null || !authenticationCode.getIsVerified()) {
            throw new CommonException(ErrorCode.NOT_VERIFIED_AUTHENTICATION_CODE);
        }
    }

}
