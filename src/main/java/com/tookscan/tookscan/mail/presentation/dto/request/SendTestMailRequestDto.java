package com.tookscan.tookscan.mail.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendTestMailRequestDto(
        @JsonProperty("email")
        @Schema(description = "이메일", example = "gildong123@gmail.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "이메일 형식이 올바르지 않습니다."
        )
        String email
) {
}
