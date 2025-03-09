package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "coupons")
@SQLDelete(sql = "UPDATE coupons SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Coupon extends BaseEntity {

    /* -------------------------------------------- */
    /* Default Column ----------------------------- */
    /* -------------------------------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "code", length = 10, unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ECouponType type;

    @Column(name = "is_possible_duplicated_apply_", nullable = false)
    private boolean isPossibleDuplicatedApply;

    @Column(name = "discount_price")
    @ColumnDefault("0")
    private Integer discountPrice;

    @Column(name = "discount_percent")
    @ColumnDefault("0")
    private Integer discountPercent;

    @Column(name = "is_used", nullable = false)
    @ColumnDefault("false")
    private boolean isUsed;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date")
    private LocalDateTime endDateTime;

    /* -------------------------------------------- */
    /* One To Many Mapping ------------------------ */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "coupon")
    private List<Order> orders = new ArrayList<>();

    @Builder
    public Coupon(String name, String description, String code, ECouponType type, Integer discountPrice,
                  Integer discountPercent, LocalDateTime startDateTime, LocalDateTime endDateTime,
                  boolean isPossibleDuplicatedApply) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.type = type;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isPossibleDuplicatedApply = isPossibleDuplicatedApply;
    }

    public void updateIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDateTime) && (endDateTime == null || now.isBefore(endDateTime)) && !isUsed;
    }

    public int calculatePrice(int price) {
        return switch (type) {
            case AMOUNT -> price - discountPrice;
            case PERCENTAGE -> price - (price * discountPercent / 100);
            case DELIVERY_PRICE_FREE -> price;
        };
    }

    public String getExpirationDate() {
        return DateTimeUtil.convertLocalDateTimeToString(startDateTime)
                + " ~ " + DateTimeUtil.convertLocalDateTimeToString(endDateTime);
    }
}
