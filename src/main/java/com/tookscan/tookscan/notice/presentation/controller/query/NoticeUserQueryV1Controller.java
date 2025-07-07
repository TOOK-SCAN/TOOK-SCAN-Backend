package com.tookscan.tookscan.notice.presentation.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.notice.application.usecase.ReadUserNoticeDetailUseCase;
import com.tookscan.tookscan.notice.application.usecase.ReadUserNoticeOverviewsUseCase;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadUserNoticeDetailResponseDto;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadUserNoticeOverviewsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "Notice 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/notices")
public class NoticeUserQueryV1Controller {

    private final ReadUserNoticeDetailUseCase readUserNoticeDetailUseCase;
    private final ReadUserNoticeOverviewsUseCase readUserNoticeOverviewsUseCase;

    @Operation(summary = "공지사항 상세 조회", description = "공개된 공지사항의 상세 정보를 조회합니다.")
    @GetMapping("/{noticeId}/details")
    public ResponseDto<ReadUserNoticeDetailResponseDto> getNotice(@PathVariable Long noticeId) {
        return ResponseDto.ok(readUserNoticeDetailUseCase.execute(noticeId));
    }

    @Operation(summary = "공지사항 목록 조회", description = "공개된 공지사항 목록을 생성일 기준 최신순으로 조회합니다.")
    @GetMapping("/overviews")
    public ResponseDto<ReadUserNoticeOverviewsResponseDto> getNoticeList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        
        return ResponseDto.ok(readUserNoticeOverviewsUseCase.execute(pageable));
    }
} 