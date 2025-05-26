package com.tookscan.tookscan.security.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyPasswordRequestDto(

        @JsonProperty("password")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
