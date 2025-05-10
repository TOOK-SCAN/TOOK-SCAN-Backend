package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserDetailResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadAdminUserDetailUseCase;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadAdminUserDetailService implements ReadAdminUserDetailUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminUserDetailResponseDto execute(UUID accountId) {

        // 유저 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        return ReadAdminUserDetailResponseDto.fromEntity(user);
    }
}
