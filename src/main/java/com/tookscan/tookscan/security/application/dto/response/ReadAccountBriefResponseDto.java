package com.tookscan.tookscan.security.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAccountBriefResponseDto extends SelfValidating<ReadAccountBriefResponseDto> {

    @JsonProperty("account_type")
    @Schema(description = "계정 유형", example = "ADMIN | USER")
    @NotNull(message = "계정 유형은 필수입니다")
    private ESecurityRole accountType;

    @JsonProperty("name")
    @Schema(description = "이름", example = "홍길동")
    @NotNull(message = "이름은 필수입니다")
    private String name;

    @JsonProperty("provider")
    @Schema(description = "제공자", example = "GOOGLE")
    @NotNull(message = "제공자는 필수입니다")
    private ESecurityProvider provider;

    @Builder
    public ReadAccountBriefResponseDto(ESecurityRole accountType, String name, ESecurityProvider provider) {
        this.accountType = accountType;
        this.name = name;
        this.provider = provider;
        this.validateSelf();
    }

    public static ReadAccountBriefResponseDto fromEntity(Account account) {
        return ReadAccountBriefResponseDto.builder()
                .accountType(account.getRole())
                .name(account.getName())
                .provider(account.getProvider())
                .build();
    }
}
