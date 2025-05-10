package com.tookscan.tookscan.security.repository.impl;

import com.tookscan.tookscan.security.domain.redis.AuthenticationCodeHistory;
import com.tookscan.tookscan.security.repository.AuthenticationCodeHistoryRepository;
import com.tookscan.tookscan.security.repository.redis.AuthenticationCodeHistoryRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthenticationCodeHistoryRepositoryImpl implements AuthenticationCodeHistoryRepository {

    private final AuthenticationCodeHistoryRedisRepository authenticationCodeHistoryRedisRepository;

    @Override
    public AuthenticationCodeHistory save(AuthenticationCodeHistory authenticationCodeHistory) {
        return authenticationCodeHistoryRedisRepository.save(authenticationCodeHistory);
    }

    @Override
    public void deleteById(String value) {
        authenticationCodeHistoryRedisRepository.deleteById(value);
    }

    @Override
    public AuthenticationCodeHistory findByIdOrElseNull(String value) {
        return authenticationCodeHistoryRedisRepository.findById(value).orElse(null);
    }
}
