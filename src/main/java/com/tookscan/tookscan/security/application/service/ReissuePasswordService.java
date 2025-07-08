package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.presentation.dto.request.ReissuePasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReissuePasswordResponseDto;
import com.tookscan.tookscan.security.application.usecase.ReissuePasswordUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.domain.service.AccountService;
import com.tookscan.tookscan.security.domain.service.AuthenticationCodeService;
import com.tookscan.tookscan.security.repository.AccountRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ReissuePasswordService implements ReissuePasswordUseCase {

    private final AccountRepository accountRepository;
    private final AuthenticationCodeRepository authenticationCodeRepository;

    private final AuthenticationCodeService authenticationCodeService;
    private final AccountService accountService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;

    @Override
    @Transactional
    public ReissuePasswordResponseDto execute(ReissuePasswordRequestDto requestDto) {

        // 인증 코드 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseThrow(requestDto.phoneNumber());

        // 인증 코드 검증 확인
        authenticationCodeService.validateAuthenticationCode(authenticationCode);

        // 계정 조회
        Account account = accountRepository.findByPhoneNumberAndSerialIdAndDeletedAtIsNullOrElseThrow(requestDto.phoneNumber(), requestDto.serialId());

        // 비밀번호 재발급 (랜덤한 숫자 + 영소문자 8자)
        String newPassword = generateRandomPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);

        // 비밀번호 변경
        accountService.changePassword(account, encodedPassword);

        return ReissuePasswordResponseDto.of(newPassword);
    }

    // 비밀번호 재발급을 위한 랜덤한 비밀번호 생성 메소드
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            password.append(CHAR_POOL.charAt(index));
        }

        return password.toString();
    }
}
