package com.tookscan.tookscan.account.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.dto.response.AddressResponseDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserUserDetailResponseDto extends SelfValidating<ReadUserUserDetailResponseDto> {

    @JsonProperty("name")
    @Schema(description = "이름", example = "홍길동")
    @NotNull(message = "이름은 필수입니다")
    private final String name;

    @JsonProperty("provider")
    @Schema(description = "제공자", example = "KAKAO | GOOGLE | DEFAULT")
    @NotNull(message = "제공자는 필수입니다")
    private ESecurityProvider provider;

    @JsonProperty("serial_id")
    @Schema(description = "시리얼 ID", example = "gildong123")
    private String serialId;

    @JsonProperty("phone_number")
    @Schema(description = "전화번호", example = "01012345678")
    @NotNull(message = "전화번호는 필수입니다")
    private String phoneNumber;

    @JsonProperty("email")
    @Schema(description = "이메일", example = "gildong123@google.com")
    private String email;

    @JsonProperty("address")
    @Schema(description = "주소")
    private AddressResponseDto address;

    @JsonProperty("is_receive_email")
    @NotNull(message = "이메일 수신 여부는 null일 수 없습니다.")
    @Schema(description = "이메일 수신 여부", example = "true")
    private Boolean isReceiveEmail;

    @JsonProperty("is_receive_sms")
    @NotNull(message = "SMS 수신 여부는 null일 수 없습니다.")
    @Schema(description = "SMS 수신 여부", example = "true")
    private Boolean isReceiveSms;

    @Builder
    public ReadUserUserDetailResponseDto(String name, ESecurityProvider provider, String serialId, String phoneNumber,
                                         String email, AddressResponseDto address, Boolean isReceiveEmail, Boolean isReceiveSms) {
        this.name = name;
        this.provider = provider;
        this.serialId = serialId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.isReceiveEmail = isReceiveEmail;
        this.isReceiveSms = isReceiveSms;
        this.validateSelf();
    }

    public static ReadUserUserDetailResponseDto fromEntity(User user) {
        return ReadUserUserDetailResponseDto.builder()
                .name(user.getName())
                .provider(user.getProvider())
                .serialId(user.getSerialId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .address(AddressResponseDto.fromEntity(user.getAddress()))
                .isReceiveEmail(user.getIsReceiveEmail())
                .isReceiveSms(user.getIsReceiveSms())
                .build();
    }
}
