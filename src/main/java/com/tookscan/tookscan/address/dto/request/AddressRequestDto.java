package com.tookscan.tookscan.address.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
        @NotBlank(message = "군/구를 입력해주세요.")
        String region2DepthName,

        @JsonProperty("region_3depth_name")
        @Schema(description = "읍/면/동", example = "역삼동")
        @NotBlank(message = "읍/면/동을 입력해주세요.")
        String region3DepthName,

        @JsonProperty("region_4depth_name")
        @Schema(description = "리/가", example = "00리")
        String region4DepthName,

        @JsonProperty("address_detail")
        @Schema(description = "상세 주소", example = "테헤란로 427")
        @NotBlank(message = "상세 주소를 입력해주세요.")
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
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
        }
}
