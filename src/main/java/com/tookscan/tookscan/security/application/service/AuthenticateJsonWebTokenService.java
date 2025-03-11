package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.application.usecase.AuthenticateJsonWebTokenUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.service.AccountService;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticateJsonWebTokenService implements AuthenticateJsonWebTokenUseCase {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Override
    public CustomUserPrincipal execute(UUID accountId) {
        Account account = accountRepository.findByIdOrElseThrow(accountId);

        return accountService.createCustomUserPrincipalByAccount(account);
    }
}
