package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ReadAdminIssuedCouponOverviewResponseDto {

    @JsonProperty("page_info")
    private PageInfoDto pageInfoDto;

    @JsonProperty("issued_coupons")
    private List<IssuedCouponOverviewDto> issuedCouponOverviewDto;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("name")
    private String name;

    @Getter
    public static class IssuedCouponOverviewDto {
        @JsonProperty("id")
        private String id;

        @JsonProperty("code")
        private String code;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("is_used")
        private Boolean isUsed;

        @JsonProperty("order")
        private IssuedCouponOrderDto order;

        @Getter
        public static class IssuedCouponOrderDto {
            @JsonProperty("id")
            private String id;

            @JsonProperty("order_number")
            private String orderNumber;

            @Builder
            public IssuedCouponOrderDto(String id, String orderNumber) {
                this.id = id;
                this.orderNumber = orderNumber;
            }

            public static IssuedCouponOrderDto fromEntity(Order order) {

                if (order == null) {
                    return null;
                }

                return IssuedCouponOrderDto.builder()
                        .id(order.getId().toString())
                        .orderNumber(order.getOrderNumber())
                        .build();
            }
        }

        @Builder
        public IssuedCouponOverviewDto(String id, String code, String createdAt, Boolean isUsed, IssuedCouponOrderDto order) {
            this.id = id;
            this.code = code;
            this.createdAt = createdAt;
            this.isUsed = isUsed;
            this.order = order;
        }

        public static IssuedCouponOverviewDto fromEntity(IssuedCoupon issuedCoupon) {
            return IssuedCouponOverviewDto.builder()
                    .id(issuedCoupon.getId().toString())
                    .code(issuedCoupon.getCode())
                    .createdAt(issuedCoupon.getCreatedAt().toString())
                    .isUsed(issuedCoupon.getMaxUsedCount() != null && issuedCoupon.getUsedCount() >= issuedCoupon.getMaxUsedCount())
                    .order(IssuedCouponOrderDto.fromEntity(issuedCoupon.getUsedCoupons().size() != 1 ? null :
                                    issuedCoupon.getUsedCoupons().get(0).getOrder()))
                    .build();
        }
    }

    @Builder
    public ReadAdminIssuedCouponOverviewResponseDto(PageInfoDto pageInfoDto, List<IssuedCouponOverviewDto> issuedCouponOverviewDto, String tag, String name) {
        this.pageInfoDto = pageInfoDto;
        this.issuedCouponOverviewDto = issuedCouponOverviewDto;
        this.tag = tag;
        this.name = name;
    }

    public static ReadAdminIssuedCouponOverviewResponseDto of(Page<IssuedCoupon> issuedCouponPage, CouponTemplate couponTemplate) {
        PageInfoDto pageInfoDto = PageInfoDto.fromEntity(issuedCouponPage);

        List<IssuedCouponOverviewDto> issuedCouponOverviewDto = issuedCouponPage.getContent().stream()
                .map(IssuedCouponOverviewDto::fromEntity)
                .toList();

        String description = null;

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

        String name = couponTemplate.getName();
        if (description != null) {
            name += " " + description;
        }

        return ReadAdminIssuedCouponOverviewResponseDto.builder()
                .pageInfoDto(pageInfoDto)
                .issuedCouponOverviewDto(issuedCouponOverviewDto)
                .tag(couponTemplate.getTag() != null ? couponTemplate.getTag() : "랜덤 생성")
                .name(name)
                .build();
    }
}
