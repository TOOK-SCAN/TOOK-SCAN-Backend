package com.tookscan.tookscan.account.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminGroupRequestDto(
        @JsonProperty("name")
        @NotBlank(message = "그룹 이름을 입력해주세요.")
        String name
) {
}
