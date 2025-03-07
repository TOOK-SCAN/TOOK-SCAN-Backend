package com.tookscan.tookscan.order.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import lombok.Builder;

public class ReadGuestOrderCouponDetailResponseDto extends SelfValidating<ReadGuestOrderCouponDetailResponseDto> {

    @JsonProperty("id")
    private final Long id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("type")
    private final ECouponType type;

    @JsonProperty("discount_price")
    private final Integer discountPrice;

    @JsonProperty("discount_percent")
    private final Integer discountPercent;

    @JsonProperty("expiration_date")
    private final String expirationDate;

    @Builder
    public ReadGuestOrderCouponDetailResponseDto(Long id, String name, String description, ECouponType type,
                                                 Integer discountPrice, Integer discountPercent,
                                                 String expirationDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.expirationDate = expirationDate;
        this.validateSelf();
    }

    public static ReadGuestOrderCouponDetailResponseDto fromEntity(Coupon coupon) {
        return ReadGuestOrderCouponDetailResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .type(coupon.getType())
                .discountPrice(coupon.getDiscountPrice())
                .discountPercent(coupon.getDiscountPercent())
                .expirationDate(coupon.getExpirationDate())
                .build();
    }
}
