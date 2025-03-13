package com.tookscan.tookscan.account.domain;

import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@PrimaryKeyJoinColumn(
        name = "account_id",
        foreignKey = @ForeignKey(name = "fk_user_account")
)
@DiscriminatorValue("USER")
@OnDelete(action = OnDeleteAction.CASCADE)
@DynamicUpdate
public class User extends Account {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "over_14_agreed", nullable = false)
    private LocalDateTime over14Agreed;

    @Column(name = "service_agreed", nullable = false)
    private LocalDateTime serviceAgreed;

    @Column(name = "marketing_allowed")
    private LocalDateTime marketingAllowed;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "is_receive_email", nullable = false)
    private Boolean isReceiveEmail;

    @Column(name = "is_receive_sms", nullable = false)
    private Boolean isReceiveSms;

    /* -------------------------------------------- */
    /* Embedded Column ---------------------------- */
    /* -------------------------------------------- */
    @Embedded
    private Address address;

    /* -------------------------------------------- */
    /* One To Many Mapping ------------------------ */
    /* -------------------------------------------- */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroup> userGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public User(
            ESecurityProvider provider,
            String serialId,
            String password,
            String name,
            String phoneNumber,
            LocalDateTime over14Agreed,
            LocalDateTime serviceAgreed,
            LocalDateTime marketingAllowed,
            Boolean isReceiveEmail,
            Boolean isReceiveSms
    ) {
        super(provider, serialId, password, phoneNumber);
        this.name = name;
        this.email = null;
        this.over14Agreed = over14Agreed;
        this.serviceAgreed = serviceAgreed;
        this.marketingAllowed = marketingAllowed;
        this.memo = null;
        this.address = null;
        this.isReceiveEmail = isReceiveEmail;
        this.isReceiveSms = isReceiveSms;
    }
    @Override
    public ESecurityRole getRole() {
        return ESecurityRole.USER;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhone(String phoneNumber) {
        super.updatePhoneNumber(phoneNumber);
    }

    public void updateMarketingAllowed(LocalDateTime marketingAllowed) {
        this.marketingAllowed = marketingAllowed;
    }

    public void updateIsReceiveEmail(Boolean isReceiveEmail) {
        this.isReceiveEmail = isReceiveEmail;
    }

    public void updateIsReceiveSms(Boolean isReceiveSms) {
        this.isReceiveSms = isReceiveSms;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}

