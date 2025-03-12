package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.core.dto.BaseEntity;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
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
@Table(name = "deliveries")
@SQLDelete(sql = "UPDATE deliveries SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Delivery extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "receiver_name", length = 10, nullable = false)
    private String receiverName;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "email", length = 320, nullable = false)
    private String email;

    @Column(name = "request", length = 100)
    private String request;

    @Column(name = "delivery_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EDeliveryStatus deliveryStatus;

    @Column(name = "tracking_number", length = 20)
    private String trackingNumber;

    @Column(name = "delivery_price", nullable = false)
    private Integer deliveryPrice;

    /* -------------------------------------------- */
    /* One To One Mapping ------------------------- */
    /* -------------------------------------------- */
    @OneToOne(mappedBy = "delivery")
    private Order order;

    /* -------------------------------------------- */
    /* Embedded Column ---------------------------- */
    /* -------------------------------------------- */
    @Embedded
    private Address address;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Delivery(String receiverName, String phoneNumber, String email, String request, EDeliveryStatus deliveryStatus, String trackingNumber, Address address, Integer deliveryPrice) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.request = request;
        this.deliveryStatus = deliveryStatus;
        this.trackingNumber = trackingNumber;
        this.address = address;
        this.deliveryPrice = deliveryPrice;
    }

    public void updateReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateRequest(String request) {
        this.request = request;
    }

    public void updateTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void updateDeliveryPrice(Integer deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getPhoneNumber() {
        return order.getUser() != null ? order.getUser().getPhoneNumber() : phoneNumber;
    }
}
