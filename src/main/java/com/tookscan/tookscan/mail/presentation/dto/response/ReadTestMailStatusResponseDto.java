package com.tookscan.tookscan.mail.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadTestMailStatusResponseDto extends SelfValidating<ReadTestMailStatusResponseDto> {

    @NotNull(message = "is_sent는 null일 수 없습니다.")
    @JsonProperty("is_sent")
    private final Boolean isSent;

    @Builder
    public ReadTestMailStatusResponseDto(Boolean isSent) {
        this.isSent = isSent;
        this.validateSelf();
    }

    public static ReadTestMailStatusResponseDto of(Boolean isSent) {
        return ReadTestMailStatusResponseDto.builder()
                .isSent(isSent)
                .build();
    }
}
