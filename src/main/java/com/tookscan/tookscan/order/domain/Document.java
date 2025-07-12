package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
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
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @Column(name = "scan_task_id")
    private String scanTaskId;

    @Column(name = "is_ocr_enabled", nullable = false)
    private Boolean isOcrEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_option", nullable = false)
    private ERecoveryOption recoveryOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_status", nullable = false)
    private EScanStatus scanStatus;

    @Column(name = "initial_name", nullable = false)
    private String initialName;

    @Column(name = "initial_page_count", nullable = false)
    private Integer initialPageCount;

    @Column(name = "initial_recovery_option", nullable = false)
    private ERecoveryOption initialRecoveryOption;

    @Column(name = "initial_is_ocr_enabled", nullable = false)
    private Boolean initialIsOcrEnabled;

    @Column(name = "custom_recovery_option_price")
    private Integer customRecoveryOptionPrice = null;

    /* -------------------------------------------- */
    /* Many to One Column ------------------------- */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_policy_id", nullable = false)
    private PricePolicy pricePolicy;

    /* -------------------------------------------- */
    /* One to Many Column ------------------------- */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pdf> pdfs;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Document(String name, int pageCount, ERecoveryOption recoveryOption, Order order, PricePolicy pricePolicy,
                    EScanStatus scanStatus, String initialName, Integer initialPageCount,
                    ERecoveryOption initialRecoveryOption, Boolean isOcrEnabled, Boolean initialIsOcrEnabled, Integer customRecoveryOptionPrice) {
        this.name = name;
        this.pageCount = pageCount;
        this.recoveryOption = recoveryOption;
        this.order = order;
        this.pricePolicy = pricePolicy;
        this.scanStatus = scanStatus;
        this.initialName = initialName;
        this.initialPageCount = initialPageCount;
        this.initialRecoveryOption = initialRecoveryOption;
        this.isOcrEnabled = isOcrEnabled;
        this.initialIsOcrEnabled = initialIsOcrEnabled;
        this.customRecoveryOptionPrice = customRecoveryOptionPrice;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void updateRecoveryOption(ERecoveryOption recoveryOption) {
        this.recoveryOption = recoveryOption;
    }


    public void updateScanStatus(EScanStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    public void updateScanTaskId(String scanTaskId) {
        this.scanTaskId = scanTaskId;
    }

    public void updateOcrEnabled(Boolean isOcrEnabled) {
        this.isOcrEnabled = isOcrEnabled;
    }

    public void updateCustomRecoveryOptionPrice(Integer customRecoveryOptionPrice) {
        this.customRecoveryOptionPrice = customRecoveryOptionPrice;
    }

    public int calculateDocumentPrice() {
        return pricePolicy.calculateDocumentPrice(pageCount);
    }

    public int calculateInitialDocumentPrice() {
        return pricePolicy.calculateDocumentPrice(initialPageCount);
    }

    public int calculateOneDayScanPrice() {
        if (!order.getIsOneDayScan()) {
            return 0;
        }
        return pricePolicy.calculatePriceForOneDayScan(pageCount);
    }

    public int calculateOcrPrice() {
        if (!isOcrEnabled) {
            return 0;
        }
        return pricePolicy.calculateOcrPrice(pageCount);
    }

    public int calculateInitialOcrPrice() {
        if (!initialIsOcrEnabled) {
            return 0;
        }
        return pricePolicy.calculateOcrPrice(initialPageCount);
    }

    public int calculatePrice() {
        return pricePolicy.calculatePrice(pageCount, recoveryOption, order.getIsOneDayScan(), isOcrEnabled, customRecoveryOptionPrice);
    }

    public int calculateRecoveryOptionPrice() {
        return recoveryOption.getPrice();
    }

    public int calculateInitialRecoveryOptionPrice() {
        return initialRecoveryOption.getPrice();
    }

    public int calculateInitialPrice() {
        return pricePolicy.calculatePrice(initialPageCount, initialRecoveryOption,
                order.getIsOneDayScan(), initialIsOcrEnabled, customRecoveryOptionPrice);
    }
}

