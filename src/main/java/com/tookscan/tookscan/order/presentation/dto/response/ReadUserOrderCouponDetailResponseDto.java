package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import lombok.Builder;

public class ReadUserOrderCouponDetailResponseDto extends SelfValidating<ReadUserOrderSummaryResponseDto> {

    @JsonProperty("id")
    private final String id;

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

    @Builder
    public ReadUserOrderCouponDetailResponseDto(String id, String name, String description, ECouponType type,
                                                Integer discountPrice, Integer discountPercent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.validateSelf();
    }

    public static ReadUserOrderCouponDetailResponseDto fromEntity(IssuedCoupon issuedCoupon) {
        return ReadUserOrderCouponDetailResponseDto.builder()
                .id(issuedCoupon.getId().toString())
                .name(issuedCoupon.getCouponTemplate().getName())
                .type(issuedCoupon.getCouponTemplate().getType())
                .discountPrice(issuedCoupon.getCouponTemplate().getDiscountPrice())
                .discountPercent(issuedCoupon.getCouponTemplate().getDiscountPercent())
                .build();
    }
}
