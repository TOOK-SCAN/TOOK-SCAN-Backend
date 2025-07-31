package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ReadAdminUsedCouponOverviewResponseDto {

    @JsonProperty("page_info")
    private PageInfoDto pageInfo;

    @JsonProperty("used_coupons")
    private List<UsedCouponOverviewDto> usedCoupons;

    @JsonProperty("name")
    private String name;

    @Getter
    public static class UsedCouponOverviewDto {
        @JsonProperty("id")
        private String id;

        @JsonProperty("issued_at")
        private String issuedAt;

        @JsonProperty("used_at")
        private String usedAt;

        @JsonProperty("order")
        private UsedCouponOrderDto order;

        @JsonProperty("discount_amount")
        private Integer discountAmount;

        @JsonProperty("code")
        private String code;

        @Getter
        public static class UsedCouponOrderDto {
            @JsonProperty("id")
            private String id;

            @JsonProperty("order_number")
            private String orderNumber;

            @Builder
            public UsedCouponOrderDto(String id, String orderNumber) {
                this.id = id;
                this.orderNumber = orderNumber;
            }

            public static UsedCouponOrderDto fromEntity(Order order) {
                return UsedCouponOrderDto.builder()
                        .id(order.getId().toString())
                        .orderNumber(order.getOrderNumber())
                        .build();
            }
        }

        @Builder
        public UsedCouponOverviewDto(String id, String issuedAt, String usedAt, UsedCouponOrderDto order, Integer discountAmount, String code) {
            this.id = id;
            this.issuedAt = issuedAt;
            this.usedAt = usedAt;
            this.order = order;
            this.discountAmount = discountAmount;
            this.code = code;
        }

        public static UsedCouponOverviewDto fromEntity(UsedCoupon usedCoupon) {
            return UsedCouponOverviewDto.builder()
                    .id(usedCoupon.getId().toString())
                    .issuedAt(usedCoupon.getIssuedCoupon().getCreatedAt().toString())
                    .usedAt(usedCoupon.getCreatedAt().toString())
                    .order(UsedCouponOrderDto.fromEntity(usedCoupon.getOrder()))
                    .discountAmount(usedCoupon.getIssuedCoupon().getDiscountPrice(usedCoupon.getOrder().getDocumentsTotalAmount(),
                            usedCoupon.getOrder().getDocuments().stream().mapToInt(Document::getOcrPrice).sum(), usedCoupon.getOrder().getDelivery().getDeliveryPrice()))
                    .code(usedCoupon.getIssuedCoupon().getCode())
                    .build();
        }
    }
    @Builder
    public ReadAdminUsedCouponOverviewResponseDto(PageInfoDto pageInfo, List<UsedCouponOverviewDto> usedCoupons, String name) {
        this.pageInfo = pageInfo;
        this.usedCoupons = usedCoupons;
        this.name = name;
    }

    public static ReadAdminUsedCouponOverviewResponseDto of(Page<UsedCoupon> usedCoupons, CouponTemplate couponTemplate) {

        String name = couponTemplate.getName();

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

        if (description != null) {
            name += " " + description;
        }

        List<UsedCouponOverviewDto> usedCouponOverviews = usedCoupons.stream()
                .map(UsedCouponOverviewDto::fromEntity)
                .toList();
        PageInfoDto pageInfo = PageInfoDto.fromEntity(usedCoupons);

        return ReadAdminUsedCouponOverviewResponseDto.builder()
                .pageInfo(pageInfo)
                .usedCoupons(usedCouponOverviews)
                .name(name)
                .build();
    }
}
