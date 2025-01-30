package com.tookscan.tookscan.term.application.controller.query;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.term.application.dto.response.ReadAdminTermOverviewResponseDto;
import com.tookscan.tookscan.term.application.usecase.ReadAdminTermOverviewUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class TermAdminQueryV1Controller {

    private final ReadAdminTermOverviewUseCase readAdminTermOverviewUseCase;

    /**
     * 8.2.1 (관리자) 약관 조회
     */
    @GetMapping("/terms/overviews")
    public ResponseDto<ReadAdminTermOverviewResponseDto> readAdminTermOverview(
            @RequestParam(value = "type") String type
    ) {
        return ResponseDto.ok(readAdminTermOverviewUseCase.execute(type));
    }
}
