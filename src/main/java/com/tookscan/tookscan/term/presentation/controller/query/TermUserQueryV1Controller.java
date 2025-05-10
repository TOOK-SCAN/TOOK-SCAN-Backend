package com.tookscan.tookscan.term.presentation.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.term.presentation.dto.response.ReadUserTermOverviewResponseDto;
import com.tookscan.tookscan.term.application.usecase.ReadUserTermOverviewUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Term", description = "Term 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class TermUserQueryV1Controller {

    private final ReadUserTermOverviewUseCase readUserTermOverviewUseCase;

    /**
     * 8.2.2 약관 조회
     */
    @GetMapping("/terms/overviews")
    public ResponseDto<ReadUserTermOverviewResponseDto> readUserTermOverview(
            @RequestParam(value = "type") String type
    ) {
        return ResponseDto.ok(readUserTermOverviewUseCase.execute(type));
    }
}
