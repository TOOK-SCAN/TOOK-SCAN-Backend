package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.domain.BaseEntity;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "initial_documents")
@SQLDelete(sql = "UPDATE initial_documents SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class InitialDocument extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    /* -------------------------------------------- */
    /* Option Column ------------------------------ */
    /* -------------------------------------------- */

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_option", nullable = false)
    private ERecoveryOption recoveryOption;

    @Column(name = "is_ocr_enabled", nullable = false)
    private Boolean isOcrEnabled;

    /* -------------------------------------------- */
    /* Price Column ------------------------------- */
    /* -------------------------------------------- */
    @Column(name = "cutting_price", nullable = false)
    private Integer cuttingPrice;

    @Column(name = "default_price_per_page", nullable = false)
    private Integer defaultPricePerPage;

    @Column(name = "additional_price_for_ocr", nullable = false)
    private Integer additionalPriceForOcr;

    @Column(name = "recovery_option_price", nullable = false)
    private Integer recoveryOptionPrice;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    /* -------------------------------------------- */
    /* Many to One Column ------------------------- */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_order_id", nullable = false)
    private InitialOrder initialOrder;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public InitialDocument(String name, Integer pageCount, ERecoveryOption recoveryOption,
                           Boolean isOcrEnabled, Integer cuttingPrice, Integer defaultPricePerPage,
                           Integer additionalPriceForOcr, Integer recoveryOptionPrice,
                           Integer totalAmount, InitialOrder initialOrder) {
        this.name = name;
        this.pageCount = pageCount;
        this.recoveryOption = recoveryOption;
        this.isOcrEnabled = isOcrEnabled;
        this.cuttingPrice = cuttingPrice;
        this.defaultPricePerPage = defaultPricePerPage;
        this.additionalPriceForOcr = additionalPriceForOcr;
        this.recoveryOptionPrice = recoveryOptionPrice;
        this.totalAmount = totalAmount;
        this.initialOrder = initialOrder;
    }

    public int getPagePrice() {
        return pageCount * defaultPricePerPage;
    }

    public int getOneDayScanPrice() {
        if (!initialOrder.getIsOneDayScan()) {
            return 0;
        }

        return pageCount * initialOrder.getAdditionalPriceForOneDayScan();
    }

    public int getDocumentPrice() {
        int pricePerPage = defaultPricePerPage
                + (isOcrEnabled ? additionalPriceForOcr : 0);
        return pricePerPage * pageCount + recoveryOptionPrice;
    }

    public int getOcrPrice() {
        if (!isOcrEnabled) {
            return 0;
        }

        return pageCount * additionalPriceForOcr;
    }

    public void calculateTotalAmount() {
        int pricePerPage = defaultPricePerPage
                + (isOcrEnabled ? additionalPriceForOcr : 0)
                + (initialOrder.getIsOneDayScan() ? initialOrder.getAdditionalPriceForOneDayScan(): 0);

        this.totalAmount = cuttingPrice + pricePerPage * pageCount + recoveryOptionPrice;
    }
}