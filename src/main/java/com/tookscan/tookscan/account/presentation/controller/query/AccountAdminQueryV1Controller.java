package com.tookscan.tookscan.account.presentation.controller.query;

import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminGroupBriefResponseDto;
import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserDetailResponseDto;
import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserOverviewResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadAdminGroupBriefUseCase;
import com.tookscan.tookscan.account.application.usecase.ReadAdminUserDetailUseCase;
import com.tookscan.tookscan.account.application.usecase.ReadAdminUserOverviewUseCase;
import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;

import java.util.UUID;

@Tag(name = "Account", description = "Account 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class AccountAdminQueryV1Controller {

    private final ReadAdminUserOverviewUseCase readAdminUserOverviewUseCase;
    private final ReadAdminUserDetailUseCase readAdminUserDetailUseCase;
    private final ReadAdminGroupBriefUseCase readAdminGroupBriefUseCase;

    /**
     * 3.2.3 (관리자) 유저 상세 조회
     */
    @Operation(summary = "유저 상세 조회", description = "관리자가 특정 유저의 상세 정보를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.NOT_FOUND_ACCOUNT,
        ErrorCode.ACCESS_DENIED,
        ErrorCode.INVALID_ARGUMENT
    })
    @GetMapping("/users/{id}/details")
    public ResponseDto<ReadAdminUserDetailResponseDto> readAdminUserDetail(
            @PathVariable("id") UUID id
            ) {
        return ResponseDto.ok(readAdminUserDetailUseCase.execute(id));
    }

    /**
     * 3.2.4 (관리자) 유저 리스트 조회
     */
    @Operation(summary = "유저 리스트 조회", description = "관리자가 검색 조건에 따라 유저 리스트를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.INVALID_PARAMETER_FORMAT,
        ErrorCode.INVALID_ENUM_TYPE,
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/users/overviews")
    public ResponseDto<ReadAdminUserOverviewResponseDto> readAdminUserOverview(
            @Parameter(description = "검색 타입 (id, name, email, phone)") @RequestParam(value = "search-type", required = false) String searchType,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "group-id", required = false) Long groupId,
            @Parameter(description = "로그인 제공자 (DEFAULT, KAKAO, GOOGLE, NAVER)") @RequestParam(value = "provider", required = false) ESecurityProvider provider,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size,
            @Parameter(description = "계정 상태 (all, active, inactive)") @RequestParam(value = "status", defaultValue = "all") String status,
            @Parameter(description = "정렬 기준 (created-at, name, email)") @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", defaultValue = "DESC") Direction direction
    ) {
        return ResponseDto.ok(readAdminUserOverviewUseCase.execute(
                searchType,
                search,
                groupId,
                provider != null ? provider.name() : null,
                startDate,
                endDate,
                page,
                size,
                status,
                sort,
                direction
        ));
    }

    /**
     * 3.2.5 (관리자) 그룹 간단 정보 조회
     */
    @Operation(summary = "그룹 간단 정보 조회", description = "관리자가 모든 그룹의 간단한 정보를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.ACCESS_DENIED
    })
    @GetMapping("/groups/briefs")
    public ResponseDto<ReadAdminGroupBriefResponseDto> readAdminGroupBrief() {
        return ResponseDto.ok(readAdminGroupBriefUseCase.execute());
    }
}
