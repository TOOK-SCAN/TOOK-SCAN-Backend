package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminDocumentsScanStatusResponseDto extends SelfValidating<ReadAdminDocumentsScanStatusResponseDto> {

    @JsonProperty("status")
    private final EScanStatus status;

    @Builder
    public ReadAdminDocumentsScanStatusResponseDto(EScanStatus status) {
        this.status = status;
        this.validateSelf();
    }

    public static ReadAdminDocumentsScanStatusResponseDto of(EScanStatus status) {
        return new ReadAdminDocumentsScanStatusResponseDto(status);
    }
}
