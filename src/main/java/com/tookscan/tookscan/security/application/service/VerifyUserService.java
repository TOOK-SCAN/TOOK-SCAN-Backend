package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.VerifyUserUseCase;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.domain.service.AuthenticationCodeService;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.presentation.dto.request.VerifyUserRequestDto;
import com.tookscan.tookscan.security.repository.AccountRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeHistoryRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifyUserService implements VerifyUserUseCase {

    private static final List<ESecurityProvider> SOCIAL_PROVIDERS = List.of(
            ESecurityProvider.KAKAO,
            ESecurityProvider.GOOGLE,
            ESecurityProvider.NAVER
    );

    private final AccountRepository accountRepository;
    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final AuthenticationCodeHistoryRepository authenticationCodeHistoryRepository;

    private final AuthenticationCodeService authenticationCodeService;

    private final JsonWebTokenUtil jsonWebTokenUtil;

    @Override
    public DefaultJsonWebTokenDto execute(VerifyUserRequestDto requestDto) {

        // 해당 번호에 관련된 인증번호 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseNull(requestDto.phoneNumber());

        // 인증번호 인증이 완료되었는지 확인
        authenticationCodeService.validateAuthenticationCode(authenticationCode);

        // Kakao 로 가입한 경우인지, Google 로 가입한 경우인지, Naver 로 가입한 경우인지 확인
        accountRepository.existsByPhoneNumberAndProvidersThenThrow(requestDto.phoneNumber(), SOCIAL_PROVIDERS);

        // Account 조회
        Account account = accountRepository.findByPhoneNumberAndSerialIdAndNameOrElseThrow(
                requestDto.phoneNumber(),
                requestDto.serialId(),
                requestDto.name()
        );

        // 인증번호 삭제
        authenticationCodeRepository.deleteById(requestDto.phoneNumber());

        // 인증번호 발급 이력 삭제
        authenticationCodeHistoryRepository.deleteById(requestDto.phoneNumber());

        // JWT 생성
        return jsonWebTokenUtil.generateDefaultJsonWebTokens(account.getId(), account.getRole());
    }
}
