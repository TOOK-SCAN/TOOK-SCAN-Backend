package com.tookscan.tookscan.order.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
        Long couponId
) {
    public record RequestDocument(

            @JsonProperty("name")
            @NotBlank(message = "문서 이름을 입력해주세요.")
            String name,

            @NotNull(message = "페이지 수를 입력해주세요.")
            @Min(value = 0, message = "페이지 수는 0 이상이어야 합니다.")
            @JsonProperty("page_prediction")
            Integer pagePrediction,

            @NotNull(message = "복원 옵션을 입력해주세요.")
            @JsonProperty("recovery_option")
            ERecoveryOption recoveryOption
    ) {
    }

    public record DeliveryInfo(

            @NotBlank(message = "받는 이를 입력해주세요.")
            @JsonProperty("receiver_name")
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
            @Pattern(regexp = "^[\\s\\S]{0,100}$", message = "요청사항은 100자를 초과할 수 없습니다")
            String request,

            @NotNull(message = "주소를 입력해주세요.")
            @JsonProperty("address")
            AddressRequestDto address
    ) {
    }
}