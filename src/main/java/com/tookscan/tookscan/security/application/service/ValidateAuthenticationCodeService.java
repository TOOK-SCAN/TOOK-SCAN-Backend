package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.presentation.dto.request.ValidateAuthenticationCodeRequestDto;
import com.tookscan.tookscan.security.application.usecase.ValidateAuthenticationCodeUseCase;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateAuthenticationCodeService implements ValidateAuthenticationCodeUseCase {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationCodeRepository authenticationCodeRepository;

    @Override
    public void execute(ValidateAuthenticationCodeRequestDto requestDto) {

        // 해당 전화번호로 발급된 인증코드 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseThrow(requestDto.phoneNumber());

        // 인증코드 일치 여부 확인
        if (!bCryptPasswordEncoder.matches(requestDto.authenticationCode(), authenticationCode.getValue())) {
            throw new CommonException(ErrorCode.NOT_MATCH_AUTHENTICATION_CODE);
        }

        // 인증코드 검증 여부 변경
        authenticationCode.verify();
        authenticationCodeRepository.save(authenticationCode);
    }
}
