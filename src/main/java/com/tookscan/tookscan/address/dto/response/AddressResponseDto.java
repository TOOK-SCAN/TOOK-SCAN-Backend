package com.tookscan.tookscan.address.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.core.dto.SelfValidating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class AddressResponseDto extends SelfValidating<AddressResponseDto> {
    @JsonProperty("address_name")
    @Schema(description = "주소명", example = "서울시 강남구 역삼동 역삼로1길")
    @NotNull(message = "주소명은 필수입니다")
    private final String addressName;

    @JsonProperty("region_1depth_name")
    @Schema(description = "1차 지역명", example = "서울시")
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
    @Schema(description = "4차 지역명", example = "역삼로1길")
    private final String region4DepthName;

    @JsonProperty("address_detail")
    @Schema(description = "상세 주소", example = "3층 301호")
    @NotNull(message = "상세 주소는 필수입니다")
    private final String addressDetail;

    @JsonProperty("zone_code")
    @Schema(description = "우편번호", example = "06210")
    @NotNull(message = "우편번호는 필수입니다")
    private final String zoneCode;

    @JsonProperty("longitude")
    @Schema(description = "경도", example = "127.123456")
    @NotNull(message = "경도는 필수입니다")
    @Range(min = -180, max = 180, message = "경도는 -180에서 180 사이의 값이어야 합니다")
    private final Double longitude;

    @JsonProperty("latitude")
    @Schema(description = "위도", example = "37.123456")
    @NotNull(message = "위도는 필수입니다")
    @Range(min = -90, max = 90, message = "위도는 -90에서 90 사이의 값이어야 합니다")
    private final Double latitude;

    @Builder
    public AddressResponseDto(String addressName, String region1DepthName, String region2DepthName,
                              String region3DepthName, String region4DepthName, String zoneCode, String addressDetail,
                              Double longitude,
                              Double latitude) {
        this.addressName = addressName;
        this.region1DepthName = region1DepthName;
        this.region2DepthName = region2DepthName;
        this.region3DepthName = region3DepthName;
        this.region4DepthName = region4DepthName;
        this.addressDetail = addressDetail;
        this.zoneCode = zoneCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.validateSelf();
    }

    public static AddressResponseDto fromEntity(Address address) {

        if (address == null) {
            return null;
        }

        return AddressResponseDto.builder()
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
