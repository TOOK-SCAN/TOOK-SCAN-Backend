package com.tookscan.tookscan.security.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ReissuePasswordRequestDto(

        @JsonProperty("serial_id")
        @NotBlank(message = "아이디를 입력해주세요.")
        String serialId,

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "전화번호 형식이 올바르지 않습니다. (- 없이 입력해주세요, 예: 01012345678)"
        )
        String phoneNumber
) {
}
