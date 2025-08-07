package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.domain.service.UserService;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.SignUpOauthUseCase;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.domain.service.AuthenticationCodeService;
import com.tookscan.tookscan.security.domain.service.RefreshTokenService;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpOauthRequestDto;
import com.tookscan.tookscan.security.repository.AccountRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeHistoryRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import com.tookscan.tookscan.security.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpOauthService implements SignUpOauthUseCase {

    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final AuthenticationCodeHistoryRepository authenticationCodeHistoryRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationCodeService authenticationCodeService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private final JsonWebTokenUtil jsonWebTokenUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Security",
        action = "oauth sign up",
        userType = "User"
    )
    public DefaultJsonWebTokenDto execute(String temporaryToken, SignUpOauthRequestDto requestDto) {

        // temporary Token 파싱
        Claims claims = jsonWebTokenUtil.validateToken(temporaryToken);

        // claims 으로부터 serialId, provider 추출
        String[] split = claims.get(Constants.ACCOUNT_ID_CLAIM_NAME,String.class).split(":");
        String serialId = split[0];
        ESecurityProvider provider = ESecurityProvider.valueOf(split[1]);

        // 중복된 아이디인지 확인
        accountRepository.existsBySerialIdAndProviderAndDeletedAtIsNullThenThrow(serialId, provider);

        // 중복된 전화번호인지 확인
        accountRepository.existsByPhoneNumberAndDeletedAtIsNullThenThrow(requestDto.phoneNumber());

        // 해당 번호에 관련된 인증번호 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseNull(requestDto.phoneNumber());

        // 인증번호 인증이 완료되었는지 확인
        authenticationCodeService.validateAuthenticationCode(authenticationCode);

        // 유저 생성 및 저장
        User user = userService.createUser(
                provider,
                serialId,
                bCryptPasswordEncoder.encode(UUID.randomUUID().toString()),
                requestDto.name(),
                requestDto.phoneNumber(),
                requestDto.isReceiveEmail() || requestDto.isReceiveSms(),
                requestDto.isReceiveEmail(),
                requestDto.isReceiveSms(),
                null,
                null
        );
        User savedUser = userRepository.save(user);

        // 인증번호 삭제
        authenticationCodeRepository.deleteById(requestDto.phoneNumber());

        // 인증번호 발급 이력 삭제
        authenticationCodeHistoryRepository.deleteById(requestDto.phoneNumber());

        // JWT 발급
        DefaultJsonWebTokenDto tokenDto = jsonWebTokenUtil.generateDefaultJsonWebTokens(savedUser.getId(), savedUser.getRole());

        // Refresh Token 저장
        refreshTokenRepository.save(refreshTokenService.createRefreshToken(savedUser.getId(), tokenDto.getRefreshToken()));

        LogContext.put("user_id", savedUser.getId());
        LogContext.put("account_id", savedUser.getId());
        
        return tokenDto;
    }
}
