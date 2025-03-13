package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
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
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_option", nullable = false)
    private ERecoveryOption recoveryOption;

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
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public InitialDocument(String name, int pageCount, ERecoveryOption recoveryOption, Order order,
                           PricePolicy pricePolicy) {
        this.name = name;
        this.pageCount = pageCount;
        this.recoveryOption = recoveryOption;
        this.order = order;
        this.pricePolicy = pricePolicy;
    }

    public int calculatePrice() {
        return pricePolicy.calculatePrice(pageCount, recoveryOption);
    }
}

