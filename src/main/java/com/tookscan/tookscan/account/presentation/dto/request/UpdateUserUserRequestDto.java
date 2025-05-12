package com.tookscan.tookscan.account.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateUserUserRequestDto(
        @JsonProperty("phone_number")
        @Schema(description = "휴대폰 번호", example = "01012345678")
        @NotNull(message = "휴대폰 번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "전화번호 형식이 올바르지 않습니다. (- 없이 입력해주세요, 예: 01012345678)"
        )
        String phoneNumber,

        @JsonProperty("email")
        @Schema(description = "이메일", example = "gildong123@gmail.com")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @JsonProperty("address")
        @Schema(description = "주소")
        @Valid
        AddressRequestDto address,

        @JsonProperty("is_receive_sms")
        @NotNull(message = "SMS 수신 여부를 선택해주세요.")
        @Schema(description = "SMS 수신 여부", example = "true")
        Boolean isReceiveSms,

        @JsonProperty("is_receive_email")
        @NotNull(message = "이메일 수신 여부를 선택해주세요.")
        @Schema(description = "이메일 수신 여부", example = "true")
        Boolean isReceiveEmail
) {
}
