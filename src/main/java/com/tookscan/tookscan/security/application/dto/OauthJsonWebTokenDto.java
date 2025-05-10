package com.tookscan.tookscan.security.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthJsonWebTokenDto extends SelfValidating<OauthJsonWebTokenDto> {

    @JsonProperty("temporary_token")
    private final String temporaryToken;

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    @Builder
    public OauthJsonWebTokenDto(String temporaryToken, String accessToken, String refreshToken) {
        this.temporaryToken = temporaryToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.validateSelf();
    }

}
