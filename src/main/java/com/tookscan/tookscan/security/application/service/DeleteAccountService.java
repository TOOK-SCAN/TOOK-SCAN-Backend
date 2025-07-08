package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.application.usecase.DeleteAccountUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.presentation.dto.request.DeleteAccountRequestDto;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAccountService implements DeleteAccountUseCase {

    private final AccountRepository accountRepository;

    @Override
    public void execute(UUID accountId, DeleteAccountRequestDto requestDto) {
        // 계정 조회
        Account account = accountRepository.findByIdAndDeletedAtIsNullOrElseThrow(accountId);

        // 계정 탈퇴 사유 업데이트
        account.updateReasonDeletion(requestDto.reason());
        accountRepository.save(account);

        accountRepository.deleteById(accountId);
    }
}
