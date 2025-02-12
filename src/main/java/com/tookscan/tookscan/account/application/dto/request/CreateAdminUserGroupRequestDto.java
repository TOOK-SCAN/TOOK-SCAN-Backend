package com.tookscan.tookscan.account.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record CreateAdminUserGroupRequestDto(
        @JsonProperty("user_ids")
        @NotEmpty(message = "유저를 선택해주세요.")
        List<UUID> userIds,

        @JsonProperty("group_ids")
        @NotEmpty(message = "그룹을 선택해주세요.")
        List<Long> groupIds
) {
}
