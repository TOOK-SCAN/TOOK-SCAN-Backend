package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminOrderBriefResponseDto extends SelfValidating<ReadAdminOrderBriefResponseDto> {

    @JsonProperty("files_count")
    private final Integer filesCount;

    @JsonProperty("status")
    private final EScanStatus status;

    @JsonProperty("email")
    private final String email;

    @Builder
    public ReadAdminOrderBriefResponseDto(Integer filesCount, EScanStatus status, String email) {
        this.filesCount = filesCount;
        this.status = status;
        this.email = email;
        this.validateSelf();
    }

    public static ReadAdminOrderBriefResponseDto of(Order order, int pdfCount, EScanStatus status) {
        return ReadAdminOrderBriefResponseDto.builder()
                .filesCount(pdfCount)
                .status(status)
                .email(order.getDelivery().getEmail())
                .build();
    }
}
