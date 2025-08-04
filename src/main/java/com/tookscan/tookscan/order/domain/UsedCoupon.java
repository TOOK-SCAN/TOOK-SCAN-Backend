package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "used_coupons")
@SQLDelete(sql = "UPDATE used_coupons SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class UsedCoupon extends BaseEntity {
    /* -------------------------------------------- */
    /* Many To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_coupon_id", nullable = false)
    private IssuedCoupon issuedCoupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_order_id")
    private InitialOrder initialOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* -------------------------------------------- */
    /* One To One Mapping ------------------------- */
    /* -------------------------------------------- */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Builder
    public UsedCoupon(IssuedCoupon issuedCoupon, InitialOrder initialOrder, Order order, User user) {
        this.issuedCoupon = issuedCoupon;
        this.initialOrder = initialOrder;
        this.order = order;
        this.user = user;
    }

}
