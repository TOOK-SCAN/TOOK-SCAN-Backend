package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.application.usecase.VerifyPasswordUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.presentation.dto.request.VerifyPasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.ValidationResponseDto;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyPasswordService implements VerifyPasswordUseCase {

    private final AccountRepository accountRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ValidationResponseDto execute(UUID accountId, VerifyPasswordRequestDto requestDto) {
        // Account 조회
        Account account = accountRepository.findByIdOrElseThrow(accountId);

        // 비밀번호 검증
        boolean isPasswordValid = bCryptPasswordEncoder.matches(requestDto.password(), account.getPassword());

        // 검증 결과에 따라 ValidationResponseDto 생성
        return ValidationResponseDto.of(isPasswordValid);
    }

}
