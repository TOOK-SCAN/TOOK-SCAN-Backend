package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "price_policies")
@SQLDelete(sql = "UPDATE price_policies SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class PricePolicy extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */

    @Column(name = "default_price", nullable = false)
    private Integer defaultPrice;

    @Column(name = "price_per_page", nullable = false)
    private Integer pricePerPage;

    @Column(name = "price_per_page_for_one_day_scan", nullable = false)
    private Integer pricePerPageForOneDayScan;

    @Column(name = "delivery_price", nullable = false)
    private Integer deliveryPrice;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public PricePolicy(Integer defaultPrice, Integer pricePerPage, Integer pricePerPageForOneDayScan,
                       Integer deliveryPrice, LocalDate startDate, LocalDate endDate) {
        this.defaultPrice = defaultPrice;
        this.pricePerPage = pricePerPage;
        this.pricePerPageForOneDayScan = pricePerPageForOneDayScan;
        this.deliveryPrice = deliveryPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int calculatePrice(int pageCount, ERecoveryOption recoveryOption) {
        int price = 0;
        price += defaultPrice;
        price += pricePerPage * pageCount;
        price += recoveryOption.getPrice();
        return price;
    }

    public int calculateDocumentPrice(int pageCount) {
        int price = 0;
        price += pricePerPage * pageCount;
        return price;
    }

    public int calculatePriceForOneDayScan(int pageCount, ERecoveryOption recoveryOption) {
        int price = 0;
        price += defaultPrice;
        price += pricePerPageForOneDayScan * pageCount;
        price += recoveryOption.getPrice();
        return price;
    }

    public int calculatePriceForOneDayScan(int pageCount) {
        int price = 0;
        price += pricePerPageForOneDayScan * pageCount;
        return price;
    }
}
