package com.tookscan.tookscan.core.dto;

import lombok.Builder;

@Builder
public record SendSlackErrorDto(
        Throwable e
) {
    public static SendSlackErrorDto of(
            Throwable e
    ) {
        return SendSlackErrorDto.builder()
                .e(e)
                .build();
    }
}
