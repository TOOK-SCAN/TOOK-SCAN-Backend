package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAdminOrderMemoRequestDto(

        @JsonProperty("is_as_in_progress")
        @NotNull(message = "AS 여부를 입력해주세요.")
        Boolean isAsInProgress,

        @JsonProperty("content")
    @NotBlank(message = "메모를 입력해주세요.")
    String content
) {
}