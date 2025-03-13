package com.tookscan.tookscan.term.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import lombok.Builder;

public class CreateAdminTermResponseDto extends SelfValidating<CreateAdminTermResponseDto> {

    @JsonProperty("id")
    private final String id;

    @Builder
    public CreateAdminTermResponseDto(
            String id
    ) {
        this.id = id;

        this.validateSelf();
    }

    public static CreateAdminTermResponseDto of(
            Long id
    ) {
        return CreateAdminTermResponseDto.builder()
                .id(id.toString())
                .build();
    }
}
