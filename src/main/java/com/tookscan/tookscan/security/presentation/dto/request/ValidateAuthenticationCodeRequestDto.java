package com.tookscan.tookscan.security.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ValidateAuthenticationCodeRequestDto(

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "전화번호 형식이 올바르지 않습니다. (- 없이 입력해주세요, 예: 01012345678)"
        )
        String phoneNumber,

        @JsonProperty("authentication_code")
        @NotBlank(message = "인증 코드를 입력해주세요.")
        @Pattern(regexp = "^[0-9]{6}$", message = "인증 코드는 6자리의 숫자로 이루어져 있습니다.")
        String authenticationCode
) {
}
