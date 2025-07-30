package com.tookscan.tookscan.mail.presentation.controller.command;

import com.tookscan.tookscan.core.annotation.swagger.ApiErrorCode;
import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.mail.application.usecase.SendTestMailUseCase;
import com.tookscan.tookscan.mail.presentation.dto.request.SendTestMailRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mail", description = "Mail 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class MailCommandV1Controller {
    private final SendTestMailUseCase sendTestMailUseCase;

    /**
     * 0.1.1 테스트 메일 발송
     * @param requestDto
     * @return
     */
    @Operation(summary = "테스트 메일 발송 (사용 중단)", description = "지정된 이메일 주소로 테스트 메일을 발송합니다. 10분 내에 최대 4회까지 발송 가능합니다.")
    @ApiErrorCode({
        ErrorCode.TOO_MANY_TEST_MAIL_REQUESTS,
        ErrorCode.INVALID_ARGUMENT,
        ErrorCode.EXTERNAL_SERVER_ERROR,
        ErrorCode.INTERNAL_SERVER_ERROR
    })
    @Deprecated
    @PostMapping("/test-email")
//    public ResponseDto<Void> sendTestMail(
    public ResponseDto<String> sendTestMail(
            @RequestBody @Valid SendTestMailRequestDto requestDto
    ) {
//        sendTestMailUseCase.execute(requestDto);
//        return ResponseDto.ok(null);
        return ResponseDto.ok("더 이상 사용되지 않는 API입니다.");
    }

}
