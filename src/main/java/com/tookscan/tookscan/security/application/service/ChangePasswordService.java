package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.presentation.dto.request.ChangePasswordRequestDto;
import com.tookscan.tookscan.security.application.usecase.ChangePasswordUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.service.AccountService;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final AccountRepository accountRepository;

    private final AccountService accountService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public void execute(UUID accountId, ChangePasswordRequestDto requestDto) {

        // 계정 조회
        Account account = accountRepository.findByIdOrElseThrow(accountId);

        // 이전 비밀번호 일치 여부 확인
        if (!bCryptPasswordEncoder.matches(requestDto.oldPassword(), account.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT);
        }

        // 비밀번호 변경
        accountService.changePassword(account, bCryptPasswordEncoder.encode(requestDto.newPassword()));

        // 변경된 비밀번호 저장
        accountRepository.save(account);

    }
}
