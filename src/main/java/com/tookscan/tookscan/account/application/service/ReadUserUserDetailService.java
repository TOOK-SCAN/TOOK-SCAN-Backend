package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.presentation.dto.response.ReadUserUserDetailResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadUserUserDetailUseCase;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserUserDetailService implements ReadUserUserDetailUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadUserUserDetailResponseDto execute(UUID accountId) {

        // User 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        // User 정보를 ReadUserDetailResponseDto로 변환
        return ReadUserUserDetailResponseDto.fromEntity(user);
    }
}
