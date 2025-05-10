package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.dto.OauthJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.LoginOauthUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.service.RefreshTokenService;
import com.tookscan.tookscan.security.info.CustomTemporaryUserPrincipal;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import com.tookscan.tookscan.security.repository.AccountRepository;
import com.tookscan.tookscan.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginOauthService implements LoginOauthUseCase {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenService refreshTokenService;

    private final JsonWebTokenUtil jsonWebTokenUtil;

    @Override
    public OauthJsonWebTokenDto execute(CustomTemporaryUserPrincipal principal) {

        return jsonWebTokenUtil.generateOauthJsonWebTokens(
                principal.getSerialId() + ":" + principal.getProvider()
        );
    }

    public OauthJsonWebTokenDto execute(CustomUserPrincipal principal) {
        // 임시유저가 아니라면 Account 조회
        Account account = accountRepository.findByIdOrElseThrow(principal.getId());

        // Account 정보를 이용하여 Oauth Json Web Token 생성
        OauthJsonWebTokenDto jsonWebTokenDto = jsonWebTokenUtil.generateOauthJsonWebTokens(
                account.getId(),
                account.getRole()
        );

        String refreshToken = jsonWebTokenDto.getRefreshToken();

        // Refresh Token Redis 에 저장
        if (refreshToken != null) {
            refreshTokenRepository.save(refreshTokenService.createRefreshToken(principal.getId(), refreshToken));
        }

        return jsonWebTokenDto;
    }
}
