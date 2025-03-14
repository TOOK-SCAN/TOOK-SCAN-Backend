package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.application.usecase.AuthenticateUserNameUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.service.AccountService;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUserNameService implements AuthenticateUserNameUseCase {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String serialId) throws UsernameNotFoundException {
        try {
            Account account = accountRepository.findBySerialIdAndProviderOrElseThrow(serialId, ESecurityProvider.DEFAULT);
            return accountService.createCustomUserPrincipalByAccount(account);
        } catch (CommonException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
