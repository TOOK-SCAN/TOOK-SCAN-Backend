package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminOrderMemoRequestDto(
    @JsonProperty("content")
    @NotBlank(message = "메모를 입력해주세요.")
    String content
) {
}