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

    @Builder
    public ReadSerialIdAndProviderResponseDto(String serialId, String provider) {
        this.serialId = serialId;
        this.provider = provider;

        validateSelf();
    }

    public static ReadSerialIdAndProviderResponseDto fromEntity(Account account) {
        return ReadSerialIdAndProviderResponseDto.builder()
                .serialId(account.getSerialId())
                .provider(account.getProvider().toString())
                .build();
    }
}
