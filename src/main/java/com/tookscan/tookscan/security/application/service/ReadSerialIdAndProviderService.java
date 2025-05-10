package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.security.presentation.dto.request.ReadSerialIdAndProviderRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReadSerialIdAndProviderResponseDto;
import com.tookscan.tookscan.security.application.usecase.ReadSerialIdAndProviderUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.domain.service.AuthenticationCodeService;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadSerialIdAndProviderService implements ReadSerialIdAndProviderUseCase {

    private final UserRepository userRepository;
    private final AuthenticationCodeRepository authenticationCodeRepository;

    private final AuthenticationCodeService authenticationCodeService;

    @Override
    @Transactional(readOnly = true)
    public ReadSerialIdAndProviderResponseDto execute(ReadSerialIdAndProviderRequestDto requestDto) {

        // 인증 코드 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseThrow(requestDto.phoneNumber());

        // 인증 코드 검증 확인
        authenticationCodeService.validateAuthenticationCode(authenticationCode);

        // 계정 조회
        Account account = userRepository.findByPhoneNumberAndNameOrElseThrow(requestDto.phoneNumber(), requestDto.name());

        return ReadSerialIdAndProviderResponseDto.fromEntity(account);
    }
}
