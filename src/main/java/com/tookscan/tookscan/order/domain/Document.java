package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "documents")
@SQLDelete(sql = "UPDATE documents SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Document extends BaseEntity {

    /* -------------------------------------------- */
    /* Basic Information Column ------------------ */
    /* -------------------------------------------- */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    /* -------------------------------------------- */
    /* Option Column ------------------------------ */
    /* -------------------------------------------- */
    @Column(name = "is_ocr_enabled", nullable = false)
    private Boolean isOcrEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_option", nullable = false)
    private ERecoveryOption recoveryOption;

    /* -------------------------------------------- */
    /* Price Column ---------------------------- */
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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /* -------------------------------------------- */
    /* One to Many Column ------------------------- */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pdf> pdfs = new ArrayList<>();

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Document(String name, int pageCount, ERecoveryOption recoveryOption, Order order, Boolean isOcrEnabled,
                    Integer recoveryOptionPrice, Integer cuttingPrice, Integer defaultPricePerPage,
                    Integer additionalPriceForOcr, Integer totalAmount) {
        this.name = name;
        this.pageCount = pageCount;
        this.recoveryOption = recoveryOption;
        this.order = order;
        this.isOcrEnabled = isOcrEnabled;
        this.recoveryOptionPrice = recoveryOptionPrice;
        this.cuttingPrice = cuttingPrice;
        this.defaultPricePerPage = defaultPricePerPage;
        this.additionalPriceForOcr = additionalPriceForOcr;
        this.totalAmount = totalAmount;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void updateRecoveryOption(ERecoveryOption recoveryOption) {
        this.recoveryOption = recoveryOption;
        this.recoveryOptionPrice = recoveryOption.getPrice();
    }

    public void updateOcrEnabled(Boolean isOcrEnabled, Integer additionalPriceForOcr) {
        this.isOcrEnabled = isOcrEnabled;
        this.additionalPriceForOcr = additionalPriceForOcr;
    }

    public void updateRecoveryOptionPrice(Integer recoveryOptionPrice) {
        this.recoveryOptionPrice = recoveryOptionPrice;
    }

    public void calculateTotalAmount() {
        int pricePerPage = defaultPricePerPage
                + (isOcrEnabled ? additionalPriceForOcr : 0)
                + (order.getIsOneDayScan() ? order.getAdditionalPriceForOneDayScan() : 0);

        this.totalAmount = cuttingPrice + pricePerPage * pageCount + recoveryOptionPrice;
    }

    public int getDocumentPrice() {
        int pricePerPage = defaultPricePerPage
                + (isOcrEnabled ? additionalPriceForOcr : 0);
        return pricePerPage * pageCount + recoveryOptionPrice;
    }

    public int getPagePrice() {
        return pageCount * defaultPricePerPage;
    }

    public int getOneDayScanPrice() {
        if (!order.getIsOneDayScan()) {
            return 0;
        }

        return pageCount * order.getAdditionalPriceForOneDayScan();
    }

    public int getOcrPrice() {
        if (!isOcrEnabled) {
            return 0;
        }

        return pageCount * additionalPriceForOcr;
    }
}

