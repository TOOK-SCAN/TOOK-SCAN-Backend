package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.domain.BaseEntity;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issued_coupons")
@SQLDelete(sql = "UPDATE issued_coupons SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class IssuedCoupon extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "code", length = 10, unique = true, nullable = false)
    private String code;

    @Column(name = "used_count")
    @ColumnDefault("0")
    private Integer usedCount;

    @Column(name = "max_used_count")
    @ColumnDefault("1")
    private Integer maxUsedCount;

    /* -------------------------------------------- */
    /* Many To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id", nullable = false)
    private CouponTemplate couponTemplate;

    /* -------------------------------------------- */
    /* One To Many Mapping ------------------------ */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "issuedCoupon")
    private List<UsedCoupon> usedCoupons = new ArrayList<>();

    @Builder
    public IssuedCoupon(String code, Integer usedCount, Integer maxUsedCount,
                        CouponTemplate couponTemplate) {
        this.code = code;
        this.usedCount = usedCount;
        this.maxUsedCount = maxUsedCount;
        this.couponTemplate = couponTemplate;
    }

    public Integer getDiscountPrice(Integer originalPrice, Integer ocrPrice, Integer deliveryPrice) {
        return couponTemplate.calculateDiscountPrice(originalPrice, ocrPrice, deliveryPrice);
    }

    public void useCoupon() {
        if (usedCount < maxUsedCount) {
            usedCount++;
        } else {
            throw new CommonException(ErrorCode.EXCEEDED_MAX_USED_COUPON);
        }
    }
}
