package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.application.dto.response.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.LoginByDefaultUseCase;
import com.tookscan.tookscan.security.domain.service.RefreshTokenService;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import com.tookscan.tookscan.security.repository.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginDefaultService implements LoginByDefaultUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void execute(CustomUserPrincipal principal, DefaultJsonWebTokenDto jsonWebTokenDto) {
        UUID accountId = principal.getId();
        String refreshToken = jsonWebTokenDto.getRefreshToken();

        if (refreshToken != null) {
            refreshTokenRepository.save(refreshTokenService.createRefreshToken(accountId, refreshToken));
        }
    }
}
