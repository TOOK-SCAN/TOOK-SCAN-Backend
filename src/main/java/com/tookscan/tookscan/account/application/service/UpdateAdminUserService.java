package com.tookscan.tookscan.account.application.service;

import com.tookscan.tookscan.account.application.usecase.UpdateAdminUserUseCase;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.domain.service.UserService;
import com.tookscan.tookscan.account.presentation.dto.request.UpdateAdminUserRequestDto;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateAdminUserService implements UpdateAdminUserUseCase {

    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Account",
        action = "update user",
        userType = "Admin"
    )
    public User execute(UpdateAdminUserRequestDto requestDto, UUID userId) {
        // 유저 정보 조회
        User user = userRepository.findByIdOrElseThrow(userId);

        user = userService.updateByAdmin(
                user,
                requestDto.name(),
                requestDto.phoneNumber(),
                requestDto.email(),
                requestDto.address(),
                requestDto.deliveryRequest(),
                requestDto.memo()
        );
        userRepository.save(user);

        LogContext.put("user_id", user.getId());
        return user;
    }
}
