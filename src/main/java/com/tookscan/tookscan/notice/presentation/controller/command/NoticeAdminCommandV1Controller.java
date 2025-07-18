package com.tookscan.tookscan.notice.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.notice.application.usecase.CreateAdminNoticeUseCase;
import com.tookscan.tookscan.notice.application.usecase.DeleteAdminNoticeUseCase;
import com.tookscan.tookscan.notice.application.usecase.UpdateAdminNoticeUseCase;
import com.tookscan.tookscan.notice.presentation.dto.request.CreateAdminNoticeRequestDto;
import com.tookscan.tookscan.notice.presentation.dto.request.UpdateAdminNoticeRequestDto;
import com.tookscan.tookscan.notice.presentation.dto.response.ReadAdminNoticeDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notice", description = "Notice 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins/notices")
public class NoticeAdminCommandV1Controller {

    private final CreateAdminNoticeUseCase createAdminNoticeUseCase;
    private final UpdateAdminNoticeUseCase updateAdminNoticeUseCase;
    private final DeleteAdminNoticeUseCase deleteAdminNoticeUseCase;

    @Operation(summary = "공지사항 등록", description = "새로운 공지사항을 등록합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.INTERNAL_SERVER_ERROR
    })
    @PostMapping
    public ResponseDto<Void> createNotice(
            @Valid @RequestBody CreateAdminNoticeRequestDto requestDto) {
        createAdminNoticeUseCase.execute(requestDto);
        return ResponseDto.created(null);
    }

    @Operation(summary = "공지사항 수정", description = "공지사항 정보를 수정합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_NOTICE,
        ErrorCode.INTERNAL_SERVER_ERROR
    })
    @PutMapping("/{noticeId}")
    public ResponseDto<ReadAdminNoticeDetailResponseDto> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody UpdateAdminNoticeRequestDto requestDto) {
        ReadAdminNoticeDetailResponseDto responseDto = updateAdminNoticeUseCase.execute(noticeId, requestDto);
        return ResponseDto.ok(responseDto);
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @ApiErrorCode({
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.BAD_REQUEST_PARAMETER,
        ErrorCode.NOT_FOUND_NOTICE,
        ErrorCode.INTERNAL_SERVER_ERROR
    })
    @DeleteMapping("/{noticeId}")
    public ResponseDto<Object> deleteNotice(@PathVariable Long noticeId) {
        deleteAdminNoticeUseCase.execute(noticeId);
        return ResponseDto.noContent();
    }
} 