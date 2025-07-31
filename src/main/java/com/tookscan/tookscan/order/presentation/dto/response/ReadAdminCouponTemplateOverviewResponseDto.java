package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReadAdminCouponTemplateOverviewResponseDto {

    @JsonProperty("page_info")
    private PageInfoDto pageInfo;

    @JsonProperty("coupons")
    @Valid
    private List<CouponOverviewDto> coupons;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("waiting_count")
    private Integer waitingCount;

    @JsonProperty("active_count")
    private Integer activeCount;

    @JsonProperty("inactive_count")
    private Integer inactiveCount;

    @Getter
    public static class CouponOverviewDto extends SelfValidating<CouponOverviewDto> {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("status")
        private String status;

        @JsonProperty("format")
        private String format;

        @JsonProperty("type")
        private String type;

        @JsonProperty("used_count")
        private Integer usedCount;

        @JsonProperty("max_used_count")
        private Integer maxUsedCount;

        @JsonProperty("start_at")
        private String startAt;

        @JsonProperty("end_at")
        private String endAt;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        @Builder
        public CouponOverviewDto(String id, String name, String description, String status, String format, String type,
                                 Integer usedCount, Integer maxUsedCount, String startAt, String endAt,
                                 String createdAt, String updatedAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.status = status;
            this.format = format;
            this.type = type;
            this.usedCount = usedCount;
            this.maxUsedCount = maxUsedCount;
            this.startAt = startAt;
            this.endAt = endAt;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static CouponOverviewDto fromEntity(CouponTemplate couponTemplate) {

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

            return CouponOverviewDto.builder()
                    .id(couponTemplate.getId().toString())
                    .name(couponTemplate.getName())
                    .description(description)
                    .status(couponTemplate.getStartDateTime() != null && couponTemplate.getEndDateTime() != null
                            ? couponTemplate.getStartDateTime().isAfter(LocalDateTime.now())
                                ? "대기"
                                : couponTemplate.getEndDateTime().isBefore(LocalDateTime.now())
                                    ? "완료"
                                    : "진행중"
                            : "진행중")
                    .format(couponTemplate.getFormat().getDescription())
                    .type(couponTemplate.getType().getDescription())
                    .usedCount(couponTemplate.getIssuedCoupons()
                            .stream()
                            .mapToInt(IssuedCoupon::getUsedCount)
                            .sum())
                    .maxUsedCount(couponTemplate.getIssuedCoupons()
                            .stream()
                            .mapToInt(IssuedCoupon::getMaxUsedCount)
                            .sum())
                    .startAt(couponTemplate.getStartDateTime() != null ? couponTemplate.getStartDateTime().toString() : null)
                    .endAt(couponTemplate.getEndDateTime() != null ? couponTemplate.getEndDateTime().toString() : null)
                    .createdAt(couponTemplate.getCreatedAt().toString())
                    .updatedAt(couponTemplate.getUpdatedAt() != null ? couponTemplate.getUpdatedAt().toString() : null)
                    .build();
        }
    }

    @Builder
    public ReadAdminCouponTemplateOverviewResponseDto(PageInfoDto pageInfo, List<CouponOverviewDto> coupons, Integer totalCount, Integer waitingCount, Integer activeCount, Integer inactiveCount) {
        this.pageInfo = pageInfo;
        this.coupons = coupons;
        this.totalCount = totalCount;
        this.waitingCount = waitingCount;
        this.activeCount = activeCount;
        this.inactiveCount = inactiveCount;
    }

    public static ReadAdminCouponTemplateOverviewResponseDto of(List<CouponTemplate> couponTemplates, Page<Long> pageInfo) {
        List<CouponOverviewDto> couponOverviews = couponTemplates.stream()
                .map(CouponOverviewDto::fromEntity)
                .toList();

        return ReadAdminCouponTemplateOverviewResponseDto.builder()
                .pageInfo(PageInfoDto.fromEntity(pageInfo))
                .coupons(couponOverviews)
                .totalCount(couponOverviews.size())
                .waitingCount((int) couponTemplates.stream()
                        .filter(c -> c.getStartDateTime() != null && c.getStartDateTime().isAfter(LocalDateTime.now()))
                        .count())
                .activeCount((int) couponTemplates.stream()
                        .filter(c -> (c.getStartDateTime() == null || c.getEndDateTime() == null)
                                || (c.getStartDateTime().isBefore(LocalDateTime.now()) && c.getEndDateTime().isAfter(LocalDateTime.now())))
                        .count())
                .inactiveCount((int) couponTemplates.stream()
                        .filter(c -> c.getEndDateTime() != null && c.getEndDateTime().isBefore(LocalDateTime.now()))
                        .count())
                .build();
    }
}
