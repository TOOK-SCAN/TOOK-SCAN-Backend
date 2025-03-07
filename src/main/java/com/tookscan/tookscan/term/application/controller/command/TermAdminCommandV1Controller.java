package com.tookscan.tookscan.term.application.controller.command;

import com.tookscan.tookscan.core.dto.ResponseDto;
import com.tookscan.tookscan.term.application.dto.request.CreateAdminTermRequestDto;
import com.tookscan.tookscan.term.application.dto.request.UpdateAdminTermRequestDto;
import com.tookscan.tookscan.term.application.usecase.CreateAdminTermUseCase;
import com.tookscan.tookscan.term.application.usecase.UpdateAdminTermUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public ResponseDto<Void> createAdminTerm(
             @RequestBody @Valid CreateAdminTermRequestDto requestDto
     ) {
         createAdminTermUseCase.execute(requestDto);
         return ResponseDto.created(null);
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
