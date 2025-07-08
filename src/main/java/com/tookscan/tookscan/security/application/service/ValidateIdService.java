package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.presentation.dto.response.ValidationResponseDto;
import com.tookscan.tookscan.security.application.usecase.ValidateIdUseCase;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateIdService implements ValidateIdUseCase {

    private final AccountRepository accountRepository;

    @Override
    public ValidationResponseDto execute(String serialId) {
        return ValidationResponseDto.of(
                !accountRepository.existsBySerialIdAndDeletedAtIsNull(serialId)
        );
    }
}
