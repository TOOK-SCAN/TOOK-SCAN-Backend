package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.presentation.dto.request.DeleteAdminUserRequestDto;
import com.tookscan.tookscan.account.application.usecase.DeleteAdminUserUseCase;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAdminUserService implements DeleteAdminUserUseCase {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void execute(DeleteAdminUserRequestDto requestDto) {
        accountRepository.deleteByIdIn(requestDto.userIds());
    }
}
