package com.tookscan.tookscan.security.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.redis.RefreshToken;
import com.tookscan.tookscan.security.repository.RefreshTokenRepository;
import com.tookscan.tookscan.security.repository.redis.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRedisRepository.save(refreshToken);
    }

    @Override
    public void deleteById(UUID id) {
        refreshTokenRedisRepository.deleteById(id);
    }

    @Override
    public RefreshToken findByValueOrElseThrow(String value) {
        return refreshTokenRedisRepository.findByValue(value)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_TOKEN_ERROR));
    }
}
