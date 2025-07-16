package com.tookscan.tookscan.account.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserUserSummaryResponseDto extends SelfValidating<ReadUserUserSummaryResponseDto> {

    @JsonProperty("name")
    @Schema(description = "이름", example = "홍길동")
    @NotNull(message = "이름은 필수입니다")
    private final String name;

    @JsonProperty("phone_number")
    @Schema(description = "전화번호", example = "01012345678")
    @NotNull(message = "전화번호는 필수입니다")
    private final String phoneNumber;

    @JsonProperty("email")
    @Schema(description = "이메일", example = "gildong123@google.com")
    private final String email;

    @JsonProperty("address")
    @Schema(description = "주소")
    private final AddressResponseDto address;

    @JsonProperty("delivery_request")
    @Schema(description = "배송 요청사항", example = "문 앞에 두고 가주세요.")
    private final String deliveryRequest;

    @Builder
    public ReadUserUserSummaryResponseDto(String name, String phoneNumber, String email, AddressResponseDto address, String deliveryRequest) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.deliveryRequest = deliveryRequest;
        this.validateSelf();
    }

    public static ReadUserUserSummaryResponseDto fromEntity(User user) {
        return ReadUserUserSummaryResponseDto.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .address(user.getAddress() != null ? AddressResponseDto.fromEntity(user.getAddress()) : null)
                .deliveryRequest(user.getDeliveryRequest() != null ? user.getDeliveryRequest() : null)
                .build();
    }
}
