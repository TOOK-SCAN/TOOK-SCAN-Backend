package com.tookscan.tookscan.payment.domain;

import com.tookscan.tookscan.core.domain.BaseEntity;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import com.tookscan.tookscan.payment.domain.type.EPaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Payment extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "payment_key", length = 200, nullable = false)
    private String paymentKey;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "method")
    @Enumerated(EnumType.STRING)
    private EPaymentMethod method;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EPaymentStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "easy_payment_provider")
    private EEasyPaymentProvider easyPaymentProvider;

    @Column(name = "receipt_url")
    private String receiptUrl;

    /* -------------------------------------------- */
    /* One To One Mapping ------------------------- */
    /* -------------------------------------------- */
    @OneToOne(mappedBy = "payment")
    private Order order;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Payment(String paymentKey, String type, EPaymentMethod method, Integer totalAmount, EPaymentStatus status, LocalDateTime requestedAt, LocalDateTime approvedAt, EEasyPaymentProvider easyPaymentProvider, String receiptUrl, Order order) {
        this.paymentKey = paymentKey;
        this.type = type;
        this.method = method;
        this.totalAmount = totalAmount;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.easyPaymentProvider = easyPaymentProvider;
        this.receiptUrl = receiptUrl;
        this.order = order;
    }

    public void updateStatus(EPaymentStatus status) {
        this.status = status;
    }

    public void updateApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

}
