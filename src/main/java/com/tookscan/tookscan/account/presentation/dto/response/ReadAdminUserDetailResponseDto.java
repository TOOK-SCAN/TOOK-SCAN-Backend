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
public class ReadAdminUserDetailResponseDto extends SelfValidating<ReadAdminUserDetailResponseDto> {

    @JsonProperty("serial_id")
    @Schema(description = "시리얼 ID", example = "user123")
    private final String serialId;

    @JsonProperty("name")
    @Schema(description = "이름", example = "홍길동")
    @NotNull
    private final String name;

    @JsonProperty("provider")
    @Schema(description = "제공자", example = "KAKAO | GOOGLE | DEFAULT")
    @NotNull
    private final ESecurityProvider provider;

    @JsonProperty("phone_number")
    @Schema(description = "전화번호", example = "01012345678")
    @NotNull
    private final String phoneNumber;

    @JsonProperty("email")
    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @JsonProperty("address")
    @Schema(description = "주소")
    private final AddressResponseDto address;

    @JsonProperty("memo")
    @Schema(description = "메모", example = "VIP 고객")
    private final String memo;

    @Builder
    public ReadAdminUserDetailResponseDto(String serialId, String name, ESecurityProvider provider, String phoneNumber,
                                          String email, AddressResponseDto address, String memo) {
        this.serialId = serialId;
        this.name = name;
        this.provider = provider;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.memo = memo;
        this.validateSelf();
    }

    public static ReadAdminUserDetailResponseDto fromEntity(User user) {
        return ReadAdminUserDetailResponseDto.builder()
                .serialId(user.getSerialId())
                .name(user.getName())
                .provider(user.getProvider())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .address(AddressResponseDto.fromEntity(user.getAddress()))
                .memo(user.getMemo() != null ? user.getMemo() : null)
                .build();
    }
}
