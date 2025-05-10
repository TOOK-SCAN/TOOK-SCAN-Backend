package com.tookscan.tookscan.account.presentation.controller.command;

import com.tookscan.tookscan.account.presentation.dto.request.UpdateUserUserRequestDto;
import com.tookscan.tookscan.account.application.usecase.UpdateUserUserUseCase;
import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Account", description = "Account 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class AccountUserCommandV1Controller {

    private final UpdateUserUserUseCase updateUserUserUseCase;

    /**
     * 3.12 유저 정보 수정
     */
    @PutMapping("/v1/users")
    @Operation(summary = "유저 정보 수정", description = "유저의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "휴대폰 인증 완료 안된상태로 휴대폰 번호 수정 시도"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseDto<Void> updateUser(
            @Parameter(hidden = true) @AccountID UUID accountId,
            @Valid @RequestBody UpdateUserUserRequestDto requestDto
    ) {
        updateUserUserUseCase.execute(accountId, requestDto);
        return ResponseDto.ok(null);
    }

}
