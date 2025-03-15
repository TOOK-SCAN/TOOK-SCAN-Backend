package com.tookscan.tookscan.term.application.controller.command;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.term.application.dto.request.CreateAdminTermRequestDto;
import com.tookscan.tookscan.term.application.dto.request.UpdateAdminTermRequestDto;
import com.tookscan.tookscan.term.application.dto.response.CreateAdminTermResponseDto;
import com.tookscan.tookscan.term.application.usecase.CreateAdminTermUseCase;
import com.tookscan.tookscan.term.application.usecase.UpdateAdminTermUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Term", description = "Term 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class TermAdminCommandV1Controller {

    private final CreateAdminTermUseCase createAdminTermUseCase;
    private final UpdateAdminTermUseCase updateAdminTermUseCase;

    /**
     * 8.1.1 (관리자) 약관 추가
     */
     @PostMapping("/terms")
    public ResponseDto<CreateAdminTermResponseDto> createAdminTerm(
             @RequestBody @Valid CreateAdminTermRequestDto requestDto
     ) {
         return ResponseDto.created(createAdminTermUseCase.execute(requestDto));
     }

    /**
     * 8.4.1 (관리자) 약관 수정
     */
    @PutMapping("/terms")
    public ResponseDto<Void> updateAdminTerm(
            @RequestBody @Valid UpdateAdminTermRequestDto requestDto
    ) {
        updateAdminTermUseCase.execute(requestDto);
        return ResponseDto.ok(null);
    }
}
