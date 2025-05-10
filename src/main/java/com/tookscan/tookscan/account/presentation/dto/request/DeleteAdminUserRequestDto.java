package com.tookscan.tookscan.account.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record DeleteAdminUserRequestDto(
        @JsonProperty("user_ids")
        @NotEmpty(message = "유저를 선택해주세요.")
        List<UUID> userIds
) {
}
