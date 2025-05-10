package com.tookscan.tookscan.mail.presentation.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.mail.presentation.dto.response.ReadTestMailStatusResponseDto;
import com.tookscan.tookscan.mail.application.usecase.ReadTestMailStatusUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mail", description = "Mail 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class MailQueryV1Controller {

    private final ReadTestMailStatusUseCase readTestMailStatusUseCase;

    @GetMapping("/validation/test-email")
    public ResponseDto<ReadTestMailStatusResponseDto> readTestMailStatus(
            @RequestParam String email
    ) {
        return ResponseDto.ok(readTestMailStatusUseCase.execute(email));
    }
}
