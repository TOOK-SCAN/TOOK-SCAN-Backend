package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import com.tookscan.tookscan.core.validator.ByteSize;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record CreateUserOrderRequestDto(

        @JsonProperty("documents")
        @Valid
        @NotEmpty(message = "문서를 1개 이상 입력해주세요.")
        List<RequestDocument> documents,

        @JsonProperty("delivery_info")
        @Valid
        @NotNull(message = "배송 정보를 입력해주세요.")
        DeliveryInfo deliveryInfo,

        @JsonProperty("coupon_id")
        Long couponId,

        @JsonProperty("is_one_day_scan")
        @NotNull(message = "원데이 스캔 여부를 입력해주세요.")
        Boolean isOneDayScan
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

            @JsonProperty("is_ocr_enabled")
            @NotNull(message = "OCR 여부를 입력해주세요.")
            Boolean isOcrEnabled
    ) {
    }

    public record DeliveryInfo(

            @NotBlank(message = "받는 이를 입력해주세요.")
            @JsonProperty("receiver_name")
            @Pattern(
                    regexp = "^(?:(?:[가-힣]{2,10})|(?:[A-Za-z]{2,30}))$",
                    message = "받는 이의 이름은 한글 2~10자(6~30바이트) 또는 영문 2~30자여야 합니다."
            )
            String receiverName,

            @JsonProperty("phone_number")
            @NotBlank(message = "전화번호를 입력해주세요.")
            @Pattern(
                    regexp = "^\\d{10,11}$",
                    message = "전화번호 형식이 올바르지 않습니다. (- 없이 입력해주세요, 예: 01012345678)"
            )
            String phoneNumber,

            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "올바른 email 형식이 아닙니다.")
            @JsonProperty("email")
            String email,

            @JsonProperty("request")
            @ByteSize(min = 0, max = 150, message = "요청사항은 최대 150바이트 입니다.")
            String request,

            @JsonProperty("address")
            @Valid
            AddressRequestDto address
    ) {
    }
}