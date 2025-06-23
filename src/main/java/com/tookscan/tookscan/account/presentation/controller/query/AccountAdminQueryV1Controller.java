package com.tookscan.tookscan.account.presentation.controller.query;

import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminGroupBriefResponseDto;
import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserDetailResponseDto;
import com.tookscan.tookscan.account.presentation.dto.response.ReadAdminUserOverviewResponseDto;
import com.tookscan.tookscan.account.application.usecase.ReadAdminGroupBriefUseCase;
import com.tookscan.tookscan.account.application.usecase.ReadAdminUserDetailUseCase;
import com.tookscan.tookscan.account.application.usecase.ReadAdminUserOverviewUseCase;
import com.tookscan.tookscan.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/users/{id}/details")
    public ResponseDto<ReadAdminUserDetailResponseDto> readAdminUserDetail(
            @PathVariable("id") UUID id
            ) {
        return ResponseDto.ok(readAdminUserDetailUseCase.execute(id));
    }

    /**
     * 3.2.4 (관리자) 유저 리스트 조회
     */
    @GetMapping("/users/overviews")
    public ResponseDto<ReadAdminUserOverviewResponseDto> readAdminUserOverview(
            @RequestParam(value = "search-type", required = false) String searchType,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "group-id", required = false) Long groupId,
            @RequestParam(value = "provider", required = false) String provider,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "페이지는 1 이상이어야 합니다") Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") Integer size
    ) {
        return ResponseDto.ok(readAdminUserOverviewUseCase.execute(
                searchType,
                search,
                groupId,
                provider,
                startDate,
                endDate,
                page,
                size
        ));
    }

    /**
     * 3.2.5 (관리자) 그룹 간단 정보 조회
     */
    @GetMapping("/groups/briefs")
    public ResponseDto<ReadAdminGroupBriefResponseDto> readAdminGroupBrief() {
        return ResponseDto.ok(readAdminGroupBriefUseCase.execute());
    }
}
