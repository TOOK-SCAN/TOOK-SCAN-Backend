package com.tookscan.tookscan.notice.presentation.controller.query;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.notice.application.usecase.ReadAdminNoticeDetailUseCase;
import com.tookscan.tookscan.notice.application.usecase.ReadAdminNoticeOverviewsUseCase;
import com.tookscan.tookscan.notice.domain.type.ENoticeSearchType;
import com.tookscan.tookscan.notice.domain.type.ENoticeSortType;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeDetailResponseDto;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeOverviewsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort.Direction;

@Tag(name = "Notice", description = "Notice 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins/notices")
public class NoticeAdminQueryV1Controller {

    private final ReadAdminNoticeDetailUseCase readAdminNoticeDetailUseCase;
    private final ReadAdminNoticeOverviewsUseCase readAdminNoticeOverviewsUseCase;

    @Operation(summary = "공지사항 상세 조회", description = "공지사항의 상세 정보를 조회합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_NOTICE,
        ErrorCode.INTERNAL_SERVER_ERROR
    })
    @GetMapping("/{noticeId}/details")
    public ResponseDto<ReadAdminNoticeDetailResponseDto> getNotice(@PathVariable Long noticeId) {
        ReadAdminNoticeDetailResponseDto responseDto = readAdminNoticeDetailUseCase.execute(noticeId);
        return ResponseDto.ok(responseDto);
    }

    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다. 검색, 정렬, 필터링, 페이지네이션을 지원합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.INTERNAL_SERVER_ERROR
    })
    @GetMapping("/overviews")
    public ResponseDto<ReadAdminNoticeOverviewsResponseDto> getNoticeList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "search-type", required = false) String searchType,
            @RequestParam(value = "sort", defaultValue = "created-at") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") Direction direction,
            @RequestParam(value = "start-date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) String endDate,
            @RequestParam(value = "is-public", required = false) Boolean isPublic) {
        
        // enum 변환
        ENoticeSearchType searchTypeEnum = searchType != null ? ENoticeSearchType.fromString(searchType) : null;
        ENoticeSortType sortEnum = ENoticeSortType.fromString(sort);
        
        ReadAdminNoticeOverviewsResponseDto responseDto = readAdminNoticeOverviewsUseCase.execute(
                page, size, search, searchTypeEnum, sortEnum, direction.name().toLowerCase(), startDate, endDate, isPublic);
        return ResponseDto.ok(responseDto);
    }
} 