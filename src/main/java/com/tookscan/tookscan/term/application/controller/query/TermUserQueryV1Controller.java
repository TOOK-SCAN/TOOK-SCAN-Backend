package com.tookscan.tookscan.term.application.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.term.application.dto.response.ReadUserTermOverviewResponseDto;
import com.tookscan.tookscan.term.application.usecase.ReadUserTermOverviewUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
