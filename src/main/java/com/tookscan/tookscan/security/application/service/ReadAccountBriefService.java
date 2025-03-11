package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.application.dto.response.ReadAccountBriefResponseDto;
import com.tookscan.tookscan.security.application.usecase.ReadAccountBriefUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadAccountBriefService implements ReadAccountBriefUseCase {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAccountBriefResponseDto execute(UUID accountId) {

        // Account 조회
        Account account = accountRepository.findByIdOrElseThrow(accountId);

        // Account 정보를 ReadAccountBriefResponseDto로 변환
        return ReadAccountBriefResponseDto.fromEntity(account);
    }
}
