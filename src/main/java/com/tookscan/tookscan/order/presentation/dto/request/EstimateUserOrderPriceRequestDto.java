package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.validator.ByteSize;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record EstimateUserOrderPriceRequestDto(
        @JsonProperty("documents")
        @Valid
        @NotEmpty(message = "문서를 1개 이상 입력해주세요.")
        List<RequestDocument> documents,

        @JsonProperty("coupon_id")
        Long couponId,

        @JsonProperty("is_one_day_scan")
        @NotNull(message = "원데이 스캔 여부를 입력해주세요.")
        Boolean isOneDayScan,

        @JsonProperty("delivery_price")
        Integer deliveryPrice
) {
    public record RequestDocument(

            @JsonProperty("name")
            @NotBlank(message = "문서 이름을 입력해주세요.")
            @ByteSize(min = 2, max = 100, message = "문서 이름의 크기는 최소 2바이트 ~ 최대 100바이트 입니다.")
            @Pattern(
                    regexp = "^(?! )[A-Za-z0-9가-힣 ]{2,100}(?<! )$",
                    message = "문서 이름은 2~100자(한글/영문/숫자/내부공백)이며, 양 끝 공백 및 특수문자는 허용되지 않습니다."
            )
            String name,

            @NotNull(message = "페이지 수를 입력해주세요.")
            @Min(value = 0, message = "페이지 수는 0 이상이어야 합니다.")
            @Max(value = 10000, message = "페이지 수는 10000 이하이어야 합니다.")
            @JsonProperty("page_count")
            Integer pageCount,

            @NotNull(message = "복원 옵션을 입력해주세요.")
            @JsonProperty("recovery_option")
            ERecoveryOption recoveryOption,

            @JsonProperty("recovery_option_price")
            Integer recoveryOptionPrice,

            @JsonProperty("is_ocr_enabled")
            @NotNull(message = "OCR 여부를 입력해주세요.")
            Boolean isOcrEnabled,

            @JsonProperty(value = "is_checked")
            @NotNull(message = "체크 여부를 입력해주세요.")
            Boolean isChecked
    ) {
    }
}

