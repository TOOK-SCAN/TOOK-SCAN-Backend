package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.payment.domain.Payment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Order extends BaseEntity {

    /* -------------------------------------------- */
    /* Basic Order Information ------------------- */
    /* -------------------------------------------- */
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private EOrderStatus orderStatus;

    @Column(name = "memo", length = 500)
    private String memo;

    /* -------------------------------------------- */
    /* Date & Time Information -------------------- */
    /* -------------------------------------------- */
    @Column(name = "delivery_expiration_date", nullable = false)
    private LocalDateTime deliveryExpirationDate;

    @Column(name = "payment_expiration_date")
    private LocalDateTime paymentExpirationDate;

    @Column(name = "pdf_send_date")
    private LocalDateTime pdfSendDate;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "recovery_completed_at")
    private LocalDateTime recoveryCompletedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "all_completed_at")
    private LocalDateTime allCompletedAt;

    @Column(name = "scan_started_at")
    private LocalDateTime scanStartedAt;

    @Column(name = "scan_completed_at")
    private LocalDateTime scanCompletedAt;

    @Column(name = "recovery_started_at")
    private LocalDateTime recoveryStartedAt;

    /* -------------------------------------------- */
    /* Agreement Information --------------------- */
    /* -------------------------------------------- */
    @Column(name = "scan_copyright_compliance_agreed", nullable = false)
    private LocalDateTime scanCopyrightComplianceAgreed;

    @Column(name = "illegal_distribution_prohibition_agreed", nullable = false)
    private LocalDateTime illegalDistributionProhibitionAgreed;

    @Column(name = "cutting_agreed", nullable = false)
    private LocalDateTime cuttingAgreed;

    @Column(name = "service_provision_period_acknowledged", nullable = false)
    private LocalDateTime serviceProvisionPeriodAcknowledged;

    @Column(name = "user_verified")
    private LocalDateTime userVerified;

    @Column(name = "took_scan_assistance_agreed")
    private LocalDateTime tookScanAssistanceAgreed;

    /* -------------------------------------------- */
    /* Service Options ---------------------------- */
    /* -------------------------------------------- */
    @Column(name = "is_one_day_scan", nullable = false)
    private Boolean isOneDayScan;

    @Column(name = "is_as_in_progress", nullable = false)
    private Boolean isAsInProgress;

    /* -------------------------------------------- */
    /* Price Information -------------------------- */
    /* -------------------------------------------- */

    @Column(name = "additional_price_for_one_day_scan", nullable = false)
    private Integer additionalPriceForOneDayScan;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    /* -------------------------------------------- */
    /* One To One Mapping ------------------------- */
    /* -------------------------------------------- */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "initial_order_id")
    private InitialOrder initialOrder;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    /* -------------------------------------------- */
    /* One To Many Mapping ------------------------ */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    /* -------------------------------------------- */
    /* Many To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Order(
            String orderNumber,
            EOrderStatus orderStatus,
            LocalDateTime deliveryExpirationDate,
            LocalDateTime scanCopyrightComplianceAgreed,
            LocalDateTime illegalDistributionProhibitionAgreed,
            LocalDateTime cuttingAgreed,
            LocalDateTime serviceProvisionPeriodAcknowledged,
            User user,
            Delivery delivery,
            Coupon coupon,
            Boolean isOneDayScan,
            Boolean isAsInProgress,
            LocalDateTime arrivedAt,
            Integer totalAmount,
            Integer additionalPriceForOneDayScan
    ) {
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.deliveryExpirationDate = deliveryExpirationDate;
        this.scanCopyrightComplianceAgreed = scanCopyrightComplianceAgreed;
        this.illegalDistributionProhibitionAgreed = illegalDistributionProhibitionAgreed;
        this.cuttingAgreed = cuttingAgreed;
        this.serviceProvisionPeriodAcknowledged = serviceProvisionPeriodAcknowledged;
        this.user = user;
        this.delivery = delivery;
        this.coupon = coupon;
        this.isOneDayScan = isOneDayScan;
        this.isAsInProgress = isAsInProgress;
        this.arrivedAt = arrivedAt;
        this.totalAmount = totalAmount;
        this.additionalPriceForOneDayScan = additionalPriceForOneDayScan;
    }

    public void createMemo(String memo) {
        this.memo = memo;
    }

    public void updateOrderStatus(EOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void updateScanTermsAgreed() {
        this.userVerified = LocalDateTime.now();
        this.tookScanAssistanceAgreed = LocalDateTime.now();
    }

    public void updatePaymentExpirationDate(LocalDateTime paymentExpirationDate) {
        this.paymentExpirationDate = paymentExpirationDate;
    }

    public void updateIsOneDayScan(Boolean isOneDayScan) {
        this.isOneDayScan = isOneDayScan;
    }
    public void updatePdfSendDate(LocalDateTime pdfSendDate) {
        this.pdfSendDate = pdfSendDate;
    }

    public void updateAsInProgress(Boolean isAsInProgress) {
        this.isAsInProgress = isAsInProgress;
    }

    public void updatePayment(Payment payment) {
        this.payment = payment;
    }

    public void updateArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public void updateInitialOrder(InitialOrder initialOrder) {
        this.initialOrder = initialOrder;
    }

    public void updateRecoveryCompletedAt(LocalDateTime recoveryCompletedAt) {
        this.recoveryCompletedAt = recoveryCompletedAt;
    }

    public void updateCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void updateCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void updateAllCompletedAt(LocalDateTime allCompletedAt) {
        this.allCompletedAt = allCompletedAt;
    }

    public void updateScanStartedAt(LocalDateTime scanStartedAt) {
        this.scanStartedAt = scanStartedAt;
    }

    public void updateScanCompletedAt(LocalDateTime scanCompletedAt) {
        this.scanCompletedAt = scanCompletedAt;
    }

    public void updateRecoveryStartedAt(LocalDateTime recoveryStartedAt) {
        this.recoveryStartedAt = recoveryStartedAt;
    }

    public String getDocumentsDescription() {
        String documentName;

        if (documents.size() == 1) {
            documentName = documents.stream()
                    .findFirst()
                    .map(Document::getName)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
            return documentName;
        }
        documentName = documents.stream()
                .findFirst()
                .map(Document::getName)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
        return documentName + " 외 " + (documents.size() - 1) + "건";
    }


    public Integer getDocumentsTotalAmount() {

        return documents.stream()
                .map(Document::getTotalAmount)
                .reduce(Integer::sum)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }

    public Integer getDocumentsPrice() {
        return documents.stream()
                .map(Document::getDocumentPrice)
                .reduce(Integer::sum)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }

    public Boolean getIsAdminChecked() {
        return orderStatus.getCode() >= EOrderStatus.COMPANY_ARRIVED.getCode();
    }

    public int getDiscountAmount() {
        if (coupon == null) {
            return 0;
        }
        return coupon.getDiscountPrice(getDocumentsTotalAmount());
    }

    public boolean isDelivery() {
        return documents.stream()
                .anyMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD);
    }

    public String getPdfUrls() {
        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        return documents.stream()
                .map(doc -> {
                    List<Pdf> pdfs = doc.getPdfs();
                    if (pdfs.isEmpty()) {
                        throw new CommonException(ErrorCode.NOT_FOUND_PDF);
                    }
                    String content = "<li style=\"margin-bottom: 10px;\">";
                    content += "- \uD83D\uDCC1 " + doc.getName() + "<ul style=\"list-style: none; margin: 6px 0 0 18px; padding: 0;\">";
                    content += "<ul style=\"list-style: none; margin: 6px 0 0 18px; padding: 0;\">";
                    int pdfCount = 1;
                    for (Pdf pdf : pdfs) {
                        if (pdf.getPdfUrl() != null) {
                            if (pdfs.size() > 1) {
                                content += "<li>└ \uD83D\uDCC4<a href=\"" + pdf.getPdfUrl() + "\" style=\"color:#1a73e8; text-decoration:none;\">" + pdf.getDocument().getName() + " (" + pdfCount + ")" + "</a></li>";
                                pdfCount++;
                            }
                            else {
                                content += "<li>└ \uD83D\uDCC4<a href=\"" + pdf.getPdfUrl() + "\" style=\"color:#1a73e8; text-decoration:none;\">" + pdf.getDocument().getName() + "</a></li>";
                            }
                        } else {
                            content += "<li style=\"margin-bottom: 6px;\">- 📁 <span style=\"color:#888888;\">PDF 파일이 준비되지 않았습니다.</span></li>";
                        }
                    }
                    content += "</ul></li>";
                    return content;
                })
                .reduce((doc1, doc2) -> doc1 + "<br /> <br />" + doc2)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }


    public void calculateTotalAmount() {
        documents.forEach(Document::calculateTotalAmount);

        int total = getDocumentsTotalAmount();

        if (coupon != null) {
            total = coupon.calculatePrice(total);
        }

        if (isDelivery()) {
            total += delivery.getDeliveryPrice();
        }

        this.totalAmount = total;
    }

}
