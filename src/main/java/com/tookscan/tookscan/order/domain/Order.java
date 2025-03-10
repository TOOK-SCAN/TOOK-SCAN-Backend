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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Order extends BaseEntity {

    /* -------------------------------------------- */
    /* Default Column ----------------------------- */
    /* -------------------------------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "memo", length = 500)
    private String memo;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InitialDocument> initialDocuments = new ArrayList<>();

    /* -------------------------------------------- */
    /* Many To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Order(String orderNumber, EOrderStatus orderStatus, boolean isByUser, User user, Delivery delivery,
                 Coupon coupon) {
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.isByUser = isByUser;
        this.user = user;
        this.delivery = delivery;
        this.coupon = coupon;
    }

    /**
     * 주문 상태를 변경합니다.
     *
     * @param orderStatus 변경할 주문 상태
     */
    public void updateOrderStatus(EOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
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

        if (documents.stream()
                .anyMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD)) {
           amount += delivery.getDeliveryPrice();
        }

        if (coupon != null) {
            amount = coupon.calculatePrice(amount);
        }
        
        return amount;
    }

    public void createMemo(String memo) {
        this.memo = memo;
    }

    public String getPdfUrls() {
        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        return documents.stream()
                .map(doc -> doc.getName() + " : " +
                        "<a href=\"" + doc.getPdf().getPdfUrl() + "\" target=\"_blank\">"
                        + doc.getPdf().getPdfUrl() + "</a>")
                .reduce((doc1, doc2) -> doc1 + "<br>" + doc2)
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


}
