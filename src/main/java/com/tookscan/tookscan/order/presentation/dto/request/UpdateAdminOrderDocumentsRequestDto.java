package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateAdminOrderDocumentsRequestDto(
        @JsonProperty("documents")
        @NotEmpty(message = "문서를 선택해주세요.")
        @Valid
        List<DocumentDto> documents,

        @JsonProperty("delivery_price")
        @NotNull(message = "배송비를 입력해주세요.")
        @Min(value = 0, message = "배송비는 0 이상이어야 합니다.")
        Integer deliveryPrice
) {
    public record DocumentDto(

            @JsonProperty("id")
            Long id,

            @JsonProperty("name")
            @NotBlank(message = "문서 이름을 입력해주세요.")
            String name,

            @JsonProperty("page_count")
            @NotNull(message = "페이지 수를 입력해주세요.")
            Integer pageCount,

            @JsonProperty("recovery_option")
            @NotNull(message = "복원 옵션을 입력해주세요.")
            ERecoveryOption recoveryOption,

            @JsonProperty("additional_price")
            @NotNull(message = "추가 금액을 입력해주세요.")
            Integer additionalPrice
    ) {
    }
}
