package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_templates")
@SQLDelete(sql = "UPDATE coupons SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CouponTemplate extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private ECouponFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ECouponType type;

    @Column(name = "discount_price")
    @ColumnDefault("0")
    private Integer discountPrice;

    @Column(name = "discount_percent")
    @ColumnDefault("0")
    private Integer discountPercent;

    @Column(name = "max_discount_price")
    private Integer maxDiscountPrice;

    @Column(name = "min_order_price")
    private Integer minOrderPrice;

    @Column(name = "max_used_per_user_count")
    private Integer maxUsedPerUserCount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date")
    private LocalDateTime endDateTime;

    @Builder
    public CouponTemplate(String name, ECouponFormat format, ECouponType type, Integer discountPrice,
                          Integer discountPercent, Integer maxDiscountPrice, Integer minOrderPrice,
                          Integer maxUsedPerUserCount, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.format = format;
        this.type = type;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.maxDiscountPrice = maxDiscountPrice;
        this.minOrderPrice = minOrderPrice;
        this.maxUsedPerUserCount = maxUsedPerUserCount;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Integer calculateDiscountPrice(Integer originalPrice) {

        if (type == ECouponType.AMOUNT) {
            return Math.min(discountPrice, maxDiscountPrice != null ? maxDiscountPrice : Integer.MAX_VALUE);
        } else if (type == ECouponType.PERCENTAGE) {
            int discount = (int) (originalPrice * (discountPercent / 100.0));
            return Math.min(discount, maxDiscountPrice != null ? maxDiscountPrice : Integer.MAX_VALUE);
        }
        return 0;
    }
}
