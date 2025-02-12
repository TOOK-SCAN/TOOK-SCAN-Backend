package com.tookscan.tookscan.account.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateAdminUserRequestDto(
        @JsonProperty("name")
        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "전화번호 형식이 올바르지 않습니다. (- 없이 입력해주세요, 예: 01012345678)"
        )
        String phoneNumber,

        @JsonProperty("email")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @JsonProperty("address")
        AddressRequestDto address,

        @JsonProperty("memo")
        String memo

) {
}
