package com.tookscan.tookscan.account.presentation.controller.query;

import com.tookscan.tookscan.account.presentation.dto.response.ReadUserUserDetailResponseDto;
import com.tookscan.tookscan.account.presentation.dto.response.ReadUserUserSummaryResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadUserUserDetailUseCase;
import com.tookscan.tookscan.account.application.usecase.ReadUserUserSummaryUseCase;
import com.tookscan.tookscan.core.annotation.security.AccountID;
import com.tookscan.tookscan.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Account", description = "Account 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class AccountUserQueryV1Controller {

    private final ReadUserUserSummaryUseCase readUserUserSummaryUseCase;
    private final ReadUserUserDetailUseCase readUserUserDetailUseCase;

    /**
     * 3.2.1 유저 배송 정보 조회
     */
    @GetMapping("/v1/users/summaries")
    @Operation(summary = "유저 배송 정보 조회", description = "배송 요청 시 필요한 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseDto<ReadUserUserSummaryResponseDto> readUserSummary(
            @Parameter(hidden = true) @AccountID UUID accountId
    ) {
        return ResponseDto.ok(readUserUserSummaryUseCase.execute(accountId));
    }

    /**
     * 3.2.2 유저 상세 정보 조회
     */
    @GetMapping("/v1/users/details")
    @Operation(summary = "유저 상세 정보 조회", description = "유저의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseDto<ReadUserUserDetailResponseDto> readUserDetail(
            @Parameter(hidden = true) @AccountID UUID accountId
    ) {
        return ResponseDto.ok(readUserUserDetailUseCase.execute(accountId));
    }

}
