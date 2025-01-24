package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.account.domain.Admin;
import com.tookscan.tookscan.account.domain.service.AdminService;
import com.tookscan.tookscan.account.repository.mysql.AdminRepository;
import com.tookscan.tookscan.security.application.dto.request.AdminSignUpDefaultRequestDto;
import com.tookscan.tookscan.security.application.usecase.AdminSignUpDefaultUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSignUpDefaultService implements AdminSignUpDefaultUseCase {

    private final AdminRepository adminRepository;

    private final AdminService adminService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void execute(AdminSignUpDefaultRequestDto requestDto) {

        // 관리자 생성 및 저장
        Admin admin = adminService.createAdmin(
                requestDto.serialId(),
                bCryptPasswordEncoder.encode(requestDto.password())
        );
        adminRepository.save(admin);

    }
}
