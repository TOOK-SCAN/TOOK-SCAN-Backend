package com.tookscan.tookscan.security.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import com.tookscan.tookscan.security.repository.redis.AuthenticationCodeRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthenticationCodeRepositoryImpl implements AuthenticationCodeRepository {

    private final AuthenticationCodeRedisRepository authenticationCodeRedisRepository;

    @Override
    public void save(AuthenticationCode authenticationCode) {
        authenticationCodeRedisRepository.save(authenticationCode);
    }

    @Override
    public void deleteById(String value) {
        authenticationCodeRedisRepository.deleteById(value);
    }

    @Override
    public AuthenticationCode findByIdOrElseNull(String value) {
        return authenticationCodeRedisRepository.findById(value).orElse(null);
    }

    @Override
    public AuthenticationCode findByIdOrElseThrow(String value) {
        return authenticationCodeRedisRepository.findById(value)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_AUTHENTICATION_CODE));
    }
}
