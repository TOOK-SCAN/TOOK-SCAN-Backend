package com.tookscan.tookscan.security.application.controller;

import com.tookscan.tookscan.security.application.dto.response.ReadAccountBriefResponseDto;
import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.HeaderUtil;
import com.tookscan.tookscan.security.application.dto.request.*;
import com.tookscan.tookscan.security.application.dto.response.*;
import com.tookscan.tookscan.security.application.usecase.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    private final ReadAccountBriefUseCase readAccountBriefUseCase;

    /**
     * 1.3 JWT 재발급
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
     * 2.1 휴대폰 인증번호 발송
     */
    @PostMapping("/authentication-code")
    public ResponseDto<IssueAuthenticationCodeResponseDto> issueAuthenticationCode(
            @Valid @RequestBody IssueAuthenticationCodeRequestDto requestDto
    ) {
        return ResponseDto.created(issueAuthenticationCodeUseCase.execute(requestDto));
    }

    /**
     * 2.2 유저 회원가입
     */
    @PostMapping("/users/sign-up-default")
    public ResponseDto<Void> signUpDefault(
            @Valid @RequestBody SignUpDefaultRequestDto requestDto
    ) {
        signUpDefaultUseCase.execute(requestDto);
        return ResponseDto.created(null);
    }

    /**
     * 2.3 관리자 회원가입
     */
    @PostMapping("/admins/sign-up-default")
    public ResponseDto<Void> adminSignUpDefault(
            @Valid @RequestBody AdminSignUpDefaultRequestDto requestDto
    ) {
        adminSignUpDefaultUseCase.execute(requestDto);
        return ResponseDto.created(null);
    }

    /**
     * 2.4 소셜 회원가입
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
     * 2.5 아이디 찾기
     */
    @PostMapping("/verification/serial-id")
    public ResponseDto<ReadSerialIdAndProviderResponseDto> readSerialId(
            @Valid @RequestBody ReadSerialIdAndProviderRequestDto requestDto
    ) {
        return ResponseDto.ok(readSerialIdAndProviderUseCase.execute(requestDto));
    }

    /**
     * 2.6 아이디 중복 검사
     */
    @GetMapping("/existence/serial-id")
    public ResponseDto<ValidationResponseDto> validateId(
            @RequestParam(name = "serial-id") String serialId
    ) {
        return ResponseDto.ok(validateIdUseCase.execute(serialId));
    }

    /**
     * 2.7 인증번호 검사
     */
    @PatchMapping("/authentication-code")
    public ResponseDto<Void> validateAuthenticationCode(
            @Valid @RequestBody ValidateAuthenticationCodeRequestDto requestDto
    ) {
        validateAuthenticationCodeUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 2.8 임시 비밀번호 발급
     */
    @PatchMapping("/reissue/password")
    public ResponseDto<ReissuePasswordResponseDto> reissuePassword(
            @Valid @RequestBody ReissuePasswordRequestDto requestDto
    ) {
        return ResponseDto.ok(reissuePasswordUseCase.execute(requestDto));
    }

    /**
     * 2.9 비밀번호 변경
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
     * 2.10 회원 탈퇴
     */
    @DeleteMapping("")
    public ResponseDto<Void> deleteAccount(
            @AccountID UUID accountId
    ) {
        deleteAccountUseCase.execute(accountId);
        return ResponseDto.ok(null);
    }

    /**
     * 2.11 계정 간단 정보 조회
     */
    @GetMapping("/briefs")
    @Operation(summary = "계정 간단 정보 조회", description = "계정 유형(ADMIN, USER)과 이름을 포함한 유저의 기본 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseDto<ReadAccountBriefResponseDto> readAccountBrief(
            @AccountID UUID accountId
    ) {
        return ResponseDto.ok(readAccountBriefUseCase.execute(accountId));
    }
}
