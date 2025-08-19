package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
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

    @JsonProperty("start_date")
    private final String startDate;

    @JsonProperty("end_date")
    private final String endDate;

    @JsonProperty("type")
    private final ECouponType type;

    @JsonProperty("discount_price")
    private final Integer discountPrice;

    @JsonProperty("discount_percent")
    private final Integer discountPercent;

    @Builder
    public ReadUserOrderCouponDetailResponseDto(String id, String name, String description, String startDate, String endDate, ECouponType type,
                                                Integer discountPrice, Integer discountPercent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.validateSelf();
    }

    public static ReadUserOrderCouponDetailResponseDto fromEntity(IssuedCoupon issuedCoupon) {

        String description = null;
        CouponTemplate couponTemplate = issuedCoupon.getCouponTemplate();

        if (couponTemplate.getType().equals(ECouponType.PERCENTAGE)) {
            int percent = couponTemplate.getDiscountPercent();
            Integer minPrice = couponTemplate.getMinOrderPrice();
            Integer maxPrice = couponTemplate.getMaxDiscountPrice();

            if (minPrice != null && maxPrice != null) {
                description = String.format("%d%% 할인 (%d원 이상 / 최대 %d원)", percent, minPrice, maxPrice);
            } else if (maxPrice != null) {
                description = String.format("%d%% 할인 (최대 %d원)", percent, maxPrice);
            } else if (minPrice != null) {
                description = String.format("%d%% 할인 (%d원 이상)", percent, minPrice);
            } else {
                description = String.format("%d%% 할인", percent);
            }

        } else if (couponTemplate.getType().equals(ECouponType.AMOUNT)) {
            int amount = couponTemplate.getDiscountPrice();
            Integer minPrice = couponTemplate.getMinOrderPrice();
            Integer maxPrice = couponTemplate.getMaxDiscountPrice();

            if (minPrice != null && maxPrice != null) {
                description = String.format("%d원 할인 (%d원 이상 / 최대 %d원)", amount, minPrice, maxPrice);
            } else if (maxPrice != null) {
                description = String.format("%d원 할인 (최대 %d원)", amount, maxPrice);
            } else if (minPrice != null) {
                description = String.format("%d원 할인 (%d원 이상)", amount, minPrice);
            } else {
                description = String.format("%d원 할인", amount);
            }
        } else if (couponTemplate.getType().equals(ECouponType.OCR_FREE)) {
            description = ECouponType.OCR_FREE.getDescription();
        } else if (couponTemplate.getType().equals(ECouponType.DELIVERY_PRICE_FREE)) {
            description = ECouponType.DELIVERY_PRICE_FREE.getDescription();
        }

        return ReadUserOrderCouponDetailResponseDto.builder()
                .id(issuedCoupon.getId().toString())
                .name(issuedCoupon.getCouponTemplate().getName())
                .description(description)
                .startDate(issuedCoupon.getCouponTemplate().getStartDateTime() != null ? DateTimeUtil.convertLocalDateToDartString(issuedCoupon.getCouponTemplate().getStartDateTime().toLocalDate()) : null)
                .endDate(issuedCoupon.getCouponTemplate().getEndDateTime() != null ? DateTimeUtil.convertLocalDateToDartString(issuedCoupon.getCouponTemplate().getEndDateTime().toLocalDate()) : null)
                .type(issuedCoupon.getCouponTemplate().getType())
                .discountPrice(issuedCoupon.getCouponTemplate().getDiscountPrice())
                .discountPercent(issuedCoupon.getCouponTemplate().getDiscountPercent())
                .build();
    }
}
