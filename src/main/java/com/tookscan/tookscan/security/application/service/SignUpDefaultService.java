package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.domain.service.UserService;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.domain.service.RefreshTokenService;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpDefaultRequestDto;
import com.tookscan.tookscan.security.application.usecase.SignUpDefaultUseCase;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.domain.service.AuthenticationCodeService;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.repository.AccountRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeHistoryRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import com.tookscan.tookscan.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpDefaultService implements SignUpDefaultUseCase {

    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final AuthenticationCodeHistoryRepository authenticationCodeHistoryRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationCodeService authenticationCodeService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JsonWebTokenUtil jsonWebTokenUtil;

    @Override
    @Transactional
    public DefaultJsonWebTokenDto execute(SignUpDefaultRequestDto requestDto) {

        // 중복된 아이디인지 확인
        accountRepository.existsBySerialIdAndProviderThenThrow(requestDto.serialId(), ESecurityProvider.DEFAULT);

        // 중복된 전화번호인지 확인
        accountRepository.existsByPhoneNumberThenThrow(requestDto.phoneNumber());

        // 해당 번호에 관련된 인증번호 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseNull(requestDto.phoneNumber());

        // 인증번호 인증이 완료되었는지 확인
        authenticationCodeService.validateAuthenticationCode(authenticationCode);

        // 유저 생성 및 저장
        User user = userService.createUser(
                ESecurityProvider.DEFAULT,
                requestDto.serialId(),
                bCryptPasswordEncoder.encode(requestDto.password()),
                requestDto.name(),
                requestDto.phoneNumber(),
                requestDto.isReceiveEmail() || requestDto.isReceiveSms(),
                requestDto.isReceiveEmail(),
                requestDto.isReceiveSms()
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

        return tokenDto;
    }
}
