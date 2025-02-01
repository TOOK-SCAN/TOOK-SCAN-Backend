package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminOrderBriefResponseDto extends SelfValidating<ReadAdminOrderBriefResponseDto> {

    @JsonProperty("files_count")
    private final Integer filesCount;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("email")
    private final String email;

    @Builder
    public ReadAdminOrderBriefResponseDto(Integer filesCount, String status, String email) {
        this.filesCount = filesCount;
        this.status = status;
        this.email = email;
        this.validateSelf();
    }

    public static ReadAdminOrderBriefResponseDto fromEntity(Order order) {
        return ReadAdminOrderBriefResponseDto.builder()
                .filesCount(order.getDocuments().stream()
                        .mapToInt(document -> document.getPdf() != null ? 1 : 0)
                        .sum())
                .status(order.getOrderStatus().toDisplayScanStatusString())
                .email(order.getDelivery().getEmail())
                .build();
    }
}
