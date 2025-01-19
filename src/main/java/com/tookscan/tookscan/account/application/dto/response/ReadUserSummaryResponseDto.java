package com.tookscan.tookscan.account.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.core.dto.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserSummaryResponseDto extends SelfValidating<ReadUserSummaryResponseDto> {

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
    private final AddressDto address;

    @Builder
    public ReadUserSummaryResponseDto(String name, String phoneNumber, String email, AddressDto address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.validateSelf();
    }

    public static class AddressDto extends SelfValidating<AddressDto> {

        @JsonProperty("address_name")
        @Schema(description = "주소명", example = "서울특별시 강남구 역삼동")
        @NotNull(message = "주소명은 필수입니다")
        private final String addressName;

        @JsonProperty("region_1depth_name")
        @Schema(description = "1차 지역명", example = "서울특별시")
        @NotNull(message = "1차 지역명은 필수입니다")
        private final String region1DepthName;

        @JsonProperty("region_2depth_name")
        @Schema(description = "2차 지역명", example = "강남구")
        @NotNull(message = "2차 지역명은 필수입니다")
        private final String region2DepthName;

        @JsonProperty("region_3depth_name")
        @Schema(description = "3차 지역명", example = "역삼동")
        @NotNull(message = "3차 지역명은 필수입니다")
        private final String region3DepthName;

        @JsonProperty("region_4depth_name")
        @Schema(description = "4차 지역명", example = "00리")
        private final String region4DepthName;

        @JsonProperty("address_detail")
        @Schema(description = "상세 주소", example = "3층 301호")
        @NotNull(message = "상세 주소는 필수입니다")
        private final String addressDetail;

        @JsonProperty("longitude")
        @Schema(description = "경도", example = "127.123456")
        @NotNull(message = "경도는 필수입니다")
        private final Double longitude;

        @JsonProperty("latitude")
        @Schema(description = "위도", example = "37.123456")
        @NotNull(message = "위도는 필수입니다")
        private final Double latitude;

        @Builder
        public AddressDto(String addressName, String region1DepthName, String region2DepthName, String region3DepthName, String region4DepthName, String addressDetail, Double longitude, Double latitude) {
            this.addressName = addressName;
            this.region1DepthName = region1DepthName;
            this.region2DepthName = region2DepthName;
            this.region3DepthName = region3DepthName;
            this.region4DepthName = region4DepthName;
            this.addressDetail = addressDetail;
            this.longitude = longitude;
            this.latitude = latitude;
            this.validateSelf();
        }

        public static AddressDto fromEntity(Address address) {

            if (address == null) {
                return null;
            }

            return AddressDto.builder()
                    .addressName(address.getAddressName())
                    .region1DepthName(address.getRegion1DepthName())
                    .region2DepthName(address.getRegion2DepthName())
                    .region3DepthName(address.getRegion3DepthName())
                    .region4DepthName(address.getRegion4DepthName() != null ? address.getRegion4DepthName() : null)
                    .addressDetail(address.getAddressDetail())
                    .longitude(address.getLongitude())
                    .latitude(address.getLatitude())
                    .build();
        }
    }

    public static ReadUserSummaryResponseDto fromEntity(User user) {
        return ReadUserSummaryResponseDto.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .address(user.getAddress() != null ? AddressDto.fromEntity(user.getAddress()) : null)
                .build();
    }
}
