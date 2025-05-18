package com.tookscan.tookscan.address.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressRequestDto(
        @JsonProperty("address_name")
        @Schema(description = "주소명", example = "서울시 강남구 역삼동")
        @NotBlank(message = "주소를 입력해주세요.")
        String addressName,

        @JsonProperty("region_1depth_name")
        @Schema(description = "시/도", example = "서울특별시")
        @NotBlank(message = "시/도를 입력해주세요.")
        String region1DepthName,

        @JsonProperty("region_2depth_name")
        @Schema(description = "군/구", example = "강남구")
        String region2DepthName,

        @JsonProperty("region_3depth_name")
        @Schema(description = "읍/면/동", example = "역삼동")
        String region3DepthName,

        @JsonProperty("zone_code")
        @Schema(description = "우편번호", example = "06210")
        @NotBlank(message = "우편번호를 입력해주세요.")
        @Size(min = 5, max = 5, message = "우편번호는 5자리여야 합니다.")
        String zoneCode,

        @JsonProperty("region_4depth_name")
        @Schema(description = "리/가", example = "00리")
        String region4DepthName,

        @JsonProperty("address_detail")
        @Schema(description = "상세 주소", example = "테헤란로 427")
        @Size(max = 50, message = "상세 주소는 50자 이내로 입력해주세요.")
        String addressDetail,

        @JsonProperty("latitude")
        @Schema(description = "위도", example = "37.501087")
        @NotNull(message = "위도를 입력해주세요.")
        @DecimalMin(value = "-90.0", message = "위도는 -90도 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90도 이하여야 합니다.")
        Double latitude,

        @JsonProperty("longitude")
        @Schema(description = "경도", example = "127.043069")
        @NotNull(message = "경도를 입력해주세요.")
        @DecimalMin(value = "-180.0", message = "경도는 -180도 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180도 이하여야 합니다.")
        Double longitude
) {
        public static AddressRequestDto fromEntity(Address address) {
                return AddressRequestDto.builder()
                        .addressName(address.getAddressName())
                        .region1DepthName(address.getRegion1DepthName())
                        .region2DepthName(address.getRegion2DepthName())
                        .region3DepthName(address.getRegion3DepthName())
                        .region4DepthName(address.getRegion4DepthName())
                        .addressDetail(address.getAddressDetail())
                        .latitude(address.getLatitude())
                        .longitude(address.getLongitude())
                        .build();
        }

        public Address toEntity() {
                return Address.builder()
                        .addressName(addressName)
                        .region1DepthName(region1DepthName)
                        .region2DepthName(region2DepthName)
                        .region3DepthName(region3DepthName)
                        .region4DepthName(region4DepthName)
                        .addressDetail(addressDetail)
                        .zoneCode(zoneCode)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
        }
}
