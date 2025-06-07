package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
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
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private EOrderStatus orderStatus;

    @Column(name = "is_by_user", nullable = false)
    private boolean isByUser;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "delivery_expiration_date", nullable = false)
    private LocalDateTime deliveryExpirationDate;

    @Column(name = "payment_expiration_date")
    private LocalDateTime paymentExpirationDate;

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

    @Column(name = "is_one_day_scan")
    private Boolean isOneDayScan;

    /* -------------------------------------------- */
    /* One To One Mapping ------------------------- */
    /* -------------------------------------------- */
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
            boolean isByUser,
            LocalDateTime deliveryExpirationDate,
            LocalDateTime scanCopyrightComplianceAgreed,
            LocalDateTime illegalDistributionProhibitionAgreed,
            LocalDateTime cuttingAgreed,
            LocalDateTime serviceProvisionPeriodAcknowledged,
            User user,
            Delivery delivery,
            Coupon coupon,
            Boolean isOneDayScan
    ) {
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.isByUser = isByUser;
        this.deliveryExpirationDate = deliveryExpirationDate;
        this.scanCopyrightComplianceAgreed = scanCopyrightComplianceAgreed;
        this.illegalDistributionProhibitionAgreed = illegalDistributionProhibitionAgreed;
        this.cuttingAgreed = cuttingAgreed;
        this.serviceProvisionPeriodAcknowledged = serviceProvisionPeriodAcknowledged;
        this.user = user;
        this.delivery = delivery;
        this.coupon = coupon;
        this.isOneDayScan = isOneDayScan;
    }

    /**
     * 주문 상태를 변경합니다.
     *
     * @param orderStatus 변경할 주문 상태
     */
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

    public void finishPayment(Payment payment) {
        this.orderStatus = EOrderStatus.PAYMENT_COMPLETED;
        this.payment = payment;
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
                .map(Document::calculatePrice)
                .reduce(Integer::sum)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }

    public int getTotalAmount() {

        Integer amount = getDocumentsTotalAmount();

        if (coupon != null) {
            amount = coupon.calculatePrice(amount);
        }

        amount += delivery.getDeliveryPrice();

        return amount;
    }

    public int getDiscountAmount() {
        if (coupon == null) {
            return 0;
        }
        return coupon.getDiscountPrice(getDocumentsTotalAmount());
    }

    public void createMemo(String memo) {
        this.memo = memo;
    }

    public String getPdfUrls() {
        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        return documents.stream()
                .map(doc -> {
                    List<Pdf> pdfs = doc.getPdfs();
                    String content = doc.getName() + " :<br />";
                    for (Pdf pdf : pdfs) {
                        if (pdf.getPdfUrl() != null) {
                            content += "<a href=\"" + pdf.getPdfUrl() + "\" target=\"_blank\">" +
                                    pdf.getPdfUrl() + "</a> <br />";
                        } else {
                            content += "PDF URL이 없습니다. <br />";
                        }
                    }
                    return content;
                })
                .reduce((doc1, doc2) -> doc1 + "<br /> <br />" + doc2)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }

    public String getUserName() {
        return user != null ? user.getName() : delivery.getReceiverName();
    }

    public String getPhoneNumber() {
        return user != null ? user.getPhoneNumber() : delivery.getPhoneNumber();
    }

    public ESecurityRole getRole() {
        return user != null ? ESecurityRole.USER : ESecurityRole.GUEST;
    }

    public boolean isDelivery() {
        return documents.stream()
                .anyMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD);
    }


}
