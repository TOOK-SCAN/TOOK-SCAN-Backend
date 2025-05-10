package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DeleteAdminDocumentsRequestDto(
        @JsonProperty("document_ids")
        @NotEmpty(message = "문서를 선택해주세요.")
        List<Long> documentIds
) {
}
