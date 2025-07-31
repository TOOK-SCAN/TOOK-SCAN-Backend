package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "initial_orders")
@SQLDelete(sql = "UPDATE initial_orders SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class InitialOrder extends BaseEntity {

    /* -------------------------------------------- */
    /* Service Options ---------------------------- */
    /* -------------------------------------------- */
    @Column(name = "is_one_day_scan", nullable = false)
    private Boolean isOneDayScan;

    /* -------------------------------------------- */
    /* Price Information -------------------------- */
    /* -------------------------------------------- */
    @Column(name = "additional_price_for_one_day_scan", nullable = false)
    private Integer additionalPriceForOneDayScan;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "delivery_price", nullable = false)
    private Integer deliveryPrice;

    /* -------------------------------------------- */
    /* One to One Column -------------------------- */
    /* -------------------------------------------- */
    @OneToOne(mappedBy = "initialOrder")
    private Order order;

    /* -------------------------------------------- */
    /* One to Many Mapping ------------------------ */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "initialOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InitialDocument> initialDocuments = new ArrayList<>();

    /* -------------------------------------------- */
    /* Many To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_coupon_id")
    private UsedCoupon usedCoupon;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public InitialOrder(
            Boolean isOneDayScan,
            Integer additionalPriceForOneDayScan,
            Integer totalAmount,
            Order order,
            UsedCoupon usedCoupon,
            Integer deliveryPrice
    ) {
        this.isOneDayScan = isOneDayScan;
        this.additionalPriceForOneDayScan = additionalPriceForOneDayScan;
        this.totalAmount = totalAmount;
        this.order = order;
        this.usedCoupon = usedCoupon;
        this.deliveryPrice = deliveryPrice;
    }

    public Integer getDocumentsPrice() {
        return initialDocuments.stream()
                .map(InitialDocument::getDocumentsPrice)
                .reduce(Integer::sum)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }

    public Integer getDocumentsTotalAmount() {
        return initialDocuments.stream()
                .map(InitialDocument::getTotalAmount)
                .reduce(Integer::sum)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }

}