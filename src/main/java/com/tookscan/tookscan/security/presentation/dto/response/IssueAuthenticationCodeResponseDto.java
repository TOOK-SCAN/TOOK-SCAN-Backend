package com.tookscan.tookscan.security.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCodeHistory;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
public class IssueAuthenticationCodeResponseDto extends SelfValidating<IssueAuthenticationCodeResponseDto> {
    @JsonProperty("try_cnt")
    @Min(0)
    private final Integer tryCnt;

    @Builder
    public IssueAuthenticationCodeResponseDto(Integer tryCnt) {
        this.tryCnt = tryCnt;

        validateSelf();
    }

    public static IssueAuthenticationCodeResponseDto fromEntity(AuthenticationCodeHistory entity) {
        return IssueAuthenticationCodeResponseDto.builder()
                .tryCnt(entity.getCount())
                .build();
    }
}
