package com.tookscan.tookscan.security.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.security.domain.mysql.Account;
import lombok.Builder;

public class ReadSerialIdAndProviderResponseDto extends SelfValidating<ReadSerialIdAndProviderResponseDto> {

    @JsonProperty("serial_id")
    private final String serialId;

    @JsonProperty("provider")
    private final String provider;

    @JsonProperty("name")
    private final String name;

    @Builder
    public ReadSerialIdAndProviderResponseDto(String serialId, String provider, String name) {
        this.serialId = serialId;
        this.provider = provider;
        this.name = name;

        validateSelf();
    }

    public static ReadSerialIdAndProviderResponseDto fromEntity(Account account) {
        return ReadSerialIdAndProviderResponseDto.builder()
                .serialId(account.getSerialId())
                .provider(account.getProvider().toString())
                .name(account.getName())
                .build();
    }
}
