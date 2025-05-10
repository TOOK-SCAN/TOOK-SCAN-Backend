package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.presentation.dto.response.ReadUserUserSummaryResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadUserUserSummaryUseCase;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserUserSummaryService implements ReadUserUserSummaryUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserUserSummaryResponseDto execute(UUID accountId) {

        // User 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        // UserSummaryResponseDto로 변환하여 반환
        return ReadUserUserSummaryResponseDto.fromEntity(user);
    }
}
