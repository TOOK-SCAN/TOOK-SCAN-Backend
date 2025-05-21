package com.tookscan.tookscan.security.presentation.controller;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.HeaderUtil;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
import com.tookscan.tookscan.security.application.usecase.AdminSignUpDefaultUseCase;
import com.tookscan.tookscan.security.application.usecase.ChangePasswordUseCase;
import com.tookscan.tookscan.security.application.usecase.DeleteAccountUseCase;
import com.tookscan.tookscan.security.application.usecase.IssueAuthenticationCodeUseCase;
import com.tookscan.tookscan.security.application.usecase.ReadSerialIdAndProviderUseCase;
import com.tookscan.tookscan.security.application.usecase.ReissueJsonWebTokenUseCase;
import com.tookscan.tookscan.security.application.usecase.ReissuePasswordUseCase;
import com.tookscan.tookscan.security.application.usecase.SignUpDefaultUseCase;
import com.tookscan.tookscan.security.application.usecase.SignUpOauthUseCase;
import com.tookscan.tookscan.security.application.usecase.ValidateAuthenticationCodeUseCase;
import com.tookscan.tookscan.security.application.usecase.ValidateIdUseCase;
import com.tookscan.tookscan.security.presentation.dto.request.AdminSignUpDefaultRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ChangePasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.IssueAuthenticationCodeRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ReadSerialIdAndProviderRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ReissuePasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpDefaultRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpOauthRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ValidateAuthenticationCodeRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.IssueAuthenticationCodeResponseDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReadSerialIdAndProviderResponseDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReissuePasswordResponseDto;
import com.tookscan.tookscan.security.presentation.dto.response.ValidationResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Hidden
@RequestMapping("/v1/auth")
public class AuthController {

    private final ReissueJsonWebTokenUseCase reissueJsonWebTokenUseCase;
    private final IssueAuthenticationCodeUseCase issueAuthenticationCodeUseCase;
    private final SignUpDefaultUseCase signUpDefaultUseCase;
    private final AdminSignUpDefaultUseCase adminSignUpDefaultUseCase;
    private final SignUpOauthUseCase signUpOauthUseCase;
    private final ReadSerialIdAndProviderUseCase readSerialIdAndProviderUseCase;
    private final ValidateIdUseCase validateIdUseCase;
    private final ValidateAuthenticationCodeUseCase validateAuthenticationCodeUseCase;
    private final ReissuePasswordUseCase reissuePasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;

    /**
     * 1.2.2 JWT 재발급
     */
    @PostMapping("/reissue/token")
    public ResponseDto<DefaultJsonWebTokenDto> reissueDefaultJsonWebToken(
            HttpServletRequest request
    ) {
        String refreshToken = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_HEADER_ERROR));

        return ResponseDto.created(reissueJsonWebTokenUseCase.execute(refreshToken));
    }

    /**
     * 2.1.1 휴대폰 인증번호 발송
     */
    @PostMapping("/authentication-code")
    public ResponseDto<IssueAuthenticationCodeResponseDto> issueAuthenticationCode(
            @Valid @RequestBody IssueAuthenticationCodeRequestDto requestDto
    ) {
        return ResponseDto.created(issueAuthenticationCodeUseCase.execute(requestDto));
    }

    /**
     * 2.1.2 유저 회원가입
     */
    @PostMapping("/users/sign-up-default")
    public ResponseDto<Void> signUpDefault(
            @Valid @RequestBody SignUpDefaultRequestDto requestDto
    ) {
        signUpDefaultUseCase.execute(requestDto);
        return ResponseDto.created(null);
    }

    /**
     * 관리자 회원가입 (더 이상 사용되지 않음)
     */
    @Deprecated
    @PostMapping("/admins/sign-up-default")
//    public ResponseDto<Void> adminSignUpDefault(
    public ResponseDto<String> adminSignUpDefault(
            @Valid @RequestBody AdminSignUpDefaultRequestDto requestDto
    ) {
//        adminSignUpDefaultUseCase.execute(requestDto);
//        return ResponseDto.created(null);
        return ResponseDto.ok("관리자 회원가입은 더 이상 사용되지 않습니다.");
    }

    /**
     * 2.1.3 소셜로그인 유저 회원가입
     */
    @PostMapping("/users/sign-up-oauth")
    public ResponseDto<Void> signUpOauth(
            @Valid @RequestBody SignUpOauthRequestDto requestDto,
            HttpServletRequest request
    ) {
        String temporaryToken = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_HEADER_ERROR));
        signUpOauthUseCase.execute(temporaryToken, requestDto);
        return ResponseDto.created(null);
    }

    /**
     * 2.1.4 아이디 찾기
     */
    @PostMapping("/verification/serial-id")
    public ResponseDto<ReadSerialIdAndProviderResponseDto> readSerialId(
            @Valid @RequestBody ReadSerialIdAndProviderRequestDto requestDto
    ) {
        return ResponseDto.ok(readSerialIdAndProviderUseCase.execute(requestDto));
    }

    /**
     * 2.2.1 아이디 중복 검사
     */
    @GetMapping("/existence/serial-id")
    public ResponseDto<ValidationResponseDto> validateId(
            @RequestParam(name = "serial-id") String serialId
    ) {
        return ResponseDto.ok(validateIdUseCase.execute(serialId));
    }

    /**
     * 2.2.2 계정 간단 정보 조회 (더 이상 사용되지 않음)
     */
    @Deprecated
    @GetMapping("/briefs")
    @Operation(summary = "계정 간단 정보 조회", description = "계정 유형(ADMIN, USER)과 이름을 포함한 유저의 기본 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
//    public ResponseDto<ReadAccountBriefResponseDto> readAccountBrief(
    public ResponseDto<String> readAccountBrief(
            @AccountID UUID accountId
    ) {
        return ResponseDto.ok("계정 간단 정보 조회는 더 이상 사용되지 않습니다.");
    }

    /**
     * 2.3.1 휴대폰 인증번호 검증
     */
    @PatchMapping("/authentication-code")
    public ResponseDto<Void> validateAuthenticationCode(
            @Valid @RequestBody ValidateAuthenticationCodeRequestDto requestDto
    ) {
        validateAuthenticationCodeUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 2.3.2 임시 비밀번호 발급
     */
    @PatchMapping("/reissue/password")
    public ResponseDto<ReissuePasswordResponseDto> reissuePassword(
            @Valid @RequestBody ReissuePasswordRequestDto requestDto
    ) {
        return ResponseDto.ok(reissuePasswordUseCase.execute(requestDto));
    }

    /**
     * 2.3.3 비밀번호 변경
     */
    @PatchMapping("/password")
    public ResponseDto<Void> changePassword(
            @AccountID UUID accountId,
            @Valid @RequestBody ChangePasswordRequestDto requestDto
    ) {
        changePasswordUseCase.execute(accountId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 2.5.1 회원 탈퇴
     */
    @DeleteMapping("")
    public ResponseDto<Void> deleteAccount(
            @AccountID UUID accountId
    ) {
        deleteAccountUseCase.execute(accountId);
        return ResponseDto.ok(null);
    }
}
