package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
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

    @Column(name = "cutting_price", nullable = false)
    private Integer cuttingPrice;

    @Column(name = "default_price_per_page", nullable = false)
    private Integer defaultPricePerPage;

    @Column(name = "additional_price_for_one_day_scan", nullable = false)
    private Integer additionalPriceForOneDayScan;

    @Column(name = "additional_price_for_ocr", nullable = false)
    private Integer additionalPriceForOcr;

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
    public PricePolicy(Integer cuttingPrice, Integer defaultPricePerPage, Integer additionalPriceForOneDayScan,
                       Integer additionalPriceForOcr,
                       Integer deliveryPrice, LocalDate startDate, LocalDate endDate) {
        this.cuttingPrice = cuttingPrice;
        this.defaultPricePerPage = defaultPricePerPage;
        this.additionalPriceForOneDayScan = additionalPriceForOneDayScan;
        this.additionalPriceForOcr = additionalPriceForOcr;
        this.deliveryPrice = deliveryPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
