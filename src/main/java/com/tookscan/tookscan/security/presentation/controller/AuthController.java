package com.tookscan.tookscan.security.presentation.controller;

import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.CookieUtil;
import com.tookscan.tookscan.core.utility.HeaderUtil;
import com.tookscan.tookscan.core.utility.HttpServletUtil;
import com.tookscan.tookscan.security.application.dto.DefaultJsonWebTokenDto;
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
import com.tookscan.tookscan.security.application.usecase.ValidatePhoneNumberUseCase;
import com.tookscan.tookscan.security.application.usecase.VerifyPasswordUseCase;
import com.tookscan.tookscan.security.application.usecase.VerifyUserUseCase;
import com.tookscan.tookscan.security.presentation.dto.request.AdminSignUpDefaultRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ChangePasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.DeleteAccountRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.IssueAuthenticationCodeRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ReadSerialIdAndProviderRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ReissuePasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpDefaultRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpOauthRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.ValidateAuthenticationCodeRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.VerifyPasswordRequestDto;
import com.tookscan.tookscan.security.presentation.dto.request.VerifyUserRequestDto;
import com.tookscan.tookscan.security.presentation.dto.response.IssueAuthenticationCodeResponseDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReadSerialIdAndProviderResponseDto;
import com.tookscan.tookscan.security.presentation.dto.response.ReissuePasswordResponseDto;
import com.tookscan.tookscan.security.presentation.dto.response.ValidationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth 관련 API 입니다.")
@RequestMapping("/v1/auth")
public class AuthController {

    private final ReissueJsonWebTokenUseCase reissueJsonWebTokenUseCase;
    private final IssueAuthenticationCodeUseCase issueAuthenticationCodeUseCase;
    private final SignUpDefaultUseCase signUpDefaultUseCase;
    private final SignUpOauthUseCase signUpOauthUseCase;
    private final ReadSerialIdAndProviderUseCase readSerialIdAndProviderUseCase;
    private final ValidateIdUseCase validateIdUseCase;
    private final ValidateAuthenticationCodeUseCase validateAuthenticationCodeUseCase;
    private final ReissuePasswordUseCase reissuePasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final VerifyUserUseCase verifyUserUseCase;
    private final VerifyPasswordUseCase verifyPasswordUseCase;
    private final ValidatePhoneNumberUseCase validatePhoneNumberUseCase;

    private final HttpServletUtil httpServletUtil;

    /**
     * 1.2.2 JWT 재발급
     */
    @Operation(summary = "JWT 재발급", description = "쿠키에 저장된 Refresh Token을 사용하여 새로운 JWT 토큰을 발급합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_COOKIE_ERROR,
        ErrorCode.EXPIRED_TOKEN_ERROR,
        ErrorCode.INVALID_TOKEN_ERROR,
        ErrorCode.TOKEN_MALFORMED_ERROR,
        ErrorCode.TOKEN_TYPE_ERROR,
        ErrorCode.TOKEN_UNSUPPORTED_ERROR,
        ErrorCode.TOKEN_GENERATION_ERROR,
        ErrorCode.TOKEN_UNKNOWN_ERROR,
        ErrorCode.NOT_FOUND_ACCOUNT
    })
    @PostMapping("/reissue/token")
    public void reissueDefaultJsonWebToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String refreshToken = CookieUtil.refineCookie(request, Constants.REFRESH_TOKEN)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_COOKIE_ERROR));

        DefaultJsonWebTokenDto tokenDto = reissueJsonWebTokenUseCase.execute(refreshToken);

        httpServletUtil.onSuccessBodyResponseWithJWTCookie(response, tokenDto);
    }

    /**
     * 2.1.1 휴대폰 인증번호 발송
     */
    @Operation(summary = "휴대폰 인증번호 발송", description = "휴대폰 번호로 인증번호를 발송합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.TOO_FAST_AUTHENTICATION_CODE_REQUESTS,
        ErrorCode.TOO_MANY_AUTHENTICATION_CODE_REQUESTS,
        ErrorCode.EXTERNAL_SERVER_ERROR,
        ErrorCode.PROVIDER_ERROR
    })
    @PostMapping("/authentication-code")
    public ResponseDto<IssueAuthenticationCodeResponseDto> issueAuthenticationCode(
            @Valid @RequestBody IssueAuthenticationCodeRequestDto requestDto
    ) {
        return ResponseDto.created(issueAuthenticationCodeUseCase.execute(requestDto));
    }

    /**
     * 2.1.2 유저 회원가입
     */
    @Operation(summary = "유저 회원가입", description = "일반 회원가입을 진행하고 JWT 토큰을 발급합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ALREADY_EXIST_ID,
        ErrorCode.ALREADY_EXIST_PHONE_NUMBER,
        ErrorCode.NOT_VERIFIED_AUTHENTICATION_CODE,
        ErrorCode.NOT_FOUND_AUTHENTICATION_CODE,
        ErrorCode.NOT_MATCH_AUTHENTICATION_CODE,
        ErrorCode.TOKEN_GENERATION_ERROR
    })
    @PostMapping("/users/sign-up-default")
    public void signUpDefault(
            @Valid @RequestBody SignUpDefaultRequestDto requestDto,
            HttpServletResponse response
    ) throws IOException {

        DefaultJsonWebTokenDto tokenDto = signUpDefaultUseCase.execute(requestDto);

        httpServletUtil.onSuccessBodyResponseWithJWTCookie(response, tokenDto);
    }

    /**
     * 관리자 회원가입 (더 이상 사용되지 않음)
     */
    @Operation(summary = "관리자 회원가입 (사용 중단)", description = "관리자 회원가입 API입니다. 더 이상 사용되지 않습니다.")
    @ApiErrorCode({})
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
    @Operation(summary = "소셜로그인 유저 회원가입", description = "소셜 로그인 후 추가 정보를 입력하여 회원가입을 완료합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_COOKIE_ERROR,
        ErrorCode.EXPIRED_TOKEN_ERROR,
        ErrorCode.INVALID_TOKEN_ERROR,
        ErrorCode.TOKEN_MALFORMED_ERROR,
        ErrorCode.TOKEN_TYPE_ERROR,
        ErrorCode.TOKEN_UNSUPPORTED_ERROR,
        ErrorCode.TOKEN_UNKNOWN_ERROR,
        ErrorCode.NOT_FOUND_TEMPORARY_ACCOUNT,
        ErrorCode.ALREADY_EXIST_PHONE_NUMBER,
        ErrorCode.NOT_VERIFIED_AUTHENTICATION_CODE,
        ErrorCode.NOT_FOUND_AUTHENTICATION_CODE,
        ErrorCode.NOT_MATCH_AUTHENTICATION_CODE,
        ErrorCode.TOKEN_GENERATION_ERROR,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER
    })
    @PostMapping("/users/sign-up-oauth")
    public void signUpOauth(
            @Valid @RequestBody SignUpOauthRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String temporaryToken = CookieUtil.refineCookie(request, Constants.TEMPORARY_TOKEN)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_COOKIE_ERROR));

        DefaultJsonWebTokenDto tokenDto = signUpOauthUseCase.execute(temporaryToken, requestDto);

        httpServletUtil.onSuccessBodyResponseWithJWTCookie(response, tokenDto);
    }

    /**
     * 2.1.4 아이디 찾기
     */
    @Operation(summary = "아이디 찾기", description = "휴대폰 번호를 통해 사용자의 아이디와 로그인 제공자를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_VERIFIED_AUTHENTICATION_CODE,
        ErrorCode.NOT_FOUND_AUTHENTICATION_CODE,
        ErrorCode.NOT_MATCH_AUTHENTICATION_CODE
    })
    @PostMapping("/verification/serial-id")
    public ResponseDto<ReadSerialIdAndProviderResponseDto> readSerialId(
            @Valid @RequestBody ReadSerialIdAndProviderRequestDto requestDto
    ) {
        return ResponseDto.ok(readSerialIdAndProviderUseCase.execute(requestDto));
    }

    /**
     * 2.1.5 유저 정보 검증
     */
    @Operation(summary = "유저 정보 검증", description = "사용자의 아이디와 휴대폰 번호를 검증하고 임시 토큰을 발급합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_VERIFIED_AUTHENTICATION_CODE,
        ErrorCode.NOT_FOUND_AUTHENTICATION_CODE,
        ErrorCode.NOT_MATCH_AUTHENTICATION_CODE,
        ErrorCode.TOKEN_GENERATION_ERROR
    })
    @PostMapping("/verification/user")
    public ResponseDto<Void> verifyUser(
            HttpServletResponse response,
            @Valid @RequestBody VerifyUserRequestDto requestDto
    ) throws IOException {
        DefaultJsonWebTokenDto tokenDto = verifyUserUseCase.execute(requestDto);
        httpServletUtil.onSuccessBodyResponseWithJWTCookie(response, tokenDto);
        return ResponseDto.ok(null);
    }

    /**
     * 2.1.6 비밀번호 검증
     */
    @Operation(summary = "비밀번호 검증", description = "사용자의 현재 비밀번호를 검증합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.FAILURE_LOGIN
    })
    @PostMapping("/verification/password")
    public ResponseDto<ValidationResponseDto> verifyPassword(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @Valid @RequestBody VerifyPasswordRequestDto requestDto
    ) {
        return ResponseDto.ok(verifyPasswordUseCase.execute(accountId, requestDto));
    }

    /**
     * 2.2.1 아이디 중복 검사
     */
    @Operation(summary = "아이디 중복 검사", description = "회원가입 시 아이디의 중복 여부를 확인합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ALREADY_EXIST_ID
    })
    @GetMapping("/existence/serial-id")
    public ResponseDto<ValidationResponseDto> validateId(
            @RequestParam(name = "serial-id") String serialId
    ) {
        return ResponseDto.ok(validateIdUseCase.execute(serialId));
    }

    /**
     * 2.2.2 계정 간단 정보 조회 (더 이상 사용되지 않음)
     */
    @Operation(summary = "계정 간단 정보 조회 (사용 중단)", description = "계정 간단 정보 조회 API입니다. 더 이상 사용되지 않습니다.")
    @ApiErrorCode({})
    @Deprecated
    @GetMapping("/briefs")
//    public ResponseDto<ReadAccountBriefResponseDto> readAccountBrief(
    public ResponseDto<String> readAccountBrief(
            @Parameter(hidden = true) @AccountID UUID accountId
    ) {
        return ResponseDto.ok("계정 간단 정보 조회는 더 이상 사용되지 않습니다.");
    }

    /**
     * 2.2.3 휴대폰 번호 중복 검사
     */
    @Operation(summary = "휴대폰 번호 중복 검사", description = "회원가입 시 휴대폰 번호의 중복 여부를 확인합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.ALREADY_EXIST_PHONE_NUMBER,
        ErrorCode.KAKAO_SIGN_IN_USE,
        ErrorCode.GOOGLE_SIGN_IN_USE,
        ErrorCode.NAVER_SIGN_IN_USE
    })
    @GetMapping("/existence/phone-number")
    public ResponseDto<ValidationResponseDto> validatePhoneNumber(
            @RequestParam(name = "phone-number") String phoneNumber
    ) {
        return ResponseDto.ok(validatePhoneNumberUseCase.execute(phoneNumber));
    }

    /**
     * 2.3.1 휴대폰 인증번호 검증
     */
    @Operation(summary = "휴대폰 인증번호 검증", description = "발송된 인증번호를 검증하여 인증을 완료합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_AUTHENTICATION_CODE,
        ErrorCode.NOT_MATCH_AUTHENTICATION_CODE,
        ErrorCode.EXCEED_MAX_AUTH_COUNT
    })
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
    @Operation(summary = "임시 비밀번호 발급", description = "사용자 검증 후 임시 비밀번호를 발급합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.NOT_VERIFIED_AUTHENTICATION_CODE,
        ErrorCode.NOT_FOUND_AUTHENTICATION_CODE,
        ErrorCode.NOT_MATCH_AUTHENTICATION_CODE,
        ErrorCode.EXTERNAL_SERVER_ERROR,
        ErrorCode.PROVIDER_ERROR
    })
    @PatchMapping("/reissue/password")
    public ResponseDto<ReissuePasswordResponseDto> reissuePassword(
            @Valid @RequestBody ReissuePasswordRequestDto requestDto
    ) {
        return ResponseDto.ok(reissuePasswordUseCase.execute(requestDto));
    }

    /**
     * 2.3.3 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.FAILURE_LOGIN
    })
    @PatchMapping("/password")
    public ResponseDto<Void> changePassword(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @Valid @RequestBody ChangePasswordRequestDto requestDto
    ) {
        changePasswordUseCase.execute(accountId, requestDto);
        return ResponseDto.ok(null);
    }

    /**
     * 2.5.1 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.FAILURE_LOGIN
    })
    @DeleteMapping("")
    public ResponseDto<Void> deleteAccount(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @RequestBody DeleteAccountRequestDto requestDto
    ) {
        deleteAccountUseCase.execute(accountId, requestDto);
        return ResponseDto.ok(null);
    }
}
