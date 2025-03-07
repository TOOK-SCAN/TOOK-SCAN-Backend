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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name = "code", length = 20, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ECouponType type;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /* -------------------------------------------- */
    /* One To Many Mapping ------------------------ */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "coupon")
    private List<Order> orders = new ArrayList<>();

    @Builder
    public Coupon(String name, String description, String code, ECouponType type, Integer discountPrice,
                  Integer discountPercent, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.type = type;
        this.discountPrice = discountPrice;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isAvailable() {
        LocalDate now = LocalDate.now();
        return now.isAfter(startDate) && (endDate == null || now.isBefore(endDate));
    }

    public int calculateDiscount(int price) {
        return switch (type) {
            case AMOUNT -> discountPrice;
            case PERCENTAGE -> price * discountPercent / 100;
            case DELIVERY_PRICE -> 0;
        };
    }

    public String getExpirationDate() {
        return DateTimeUtil.convertLocalDateToString(startDate)
                + " ~ " + DateTimeUtil.convertLocalDateToString(endDate);
    }
}
