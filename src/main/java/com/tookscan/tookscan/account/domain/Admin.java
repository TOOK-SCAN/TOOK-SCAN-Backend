package com.tookscan.tookscan.account.domain;

import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.domain.type.ESecurityRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admins")
@PrimaryKeyJoinColumn(
        name = "account_id",
        foreignKey = @ForeignKey(name = "fk_admin_account")
)
@DiscriminatorValue("ADMIN")
@DynamicUpdate
@SQLDelete(sql = "UPDATE admins SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Admin extends Account {

    private static final String ADMIN_NAME = "관리자";

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Admin(
            String serialId,
            String password
    ) {
        super(ESecurityProvider.DEFAULT, serialId, password, null, null, null);
    }

    @Override
    public ESecurityRole getRole() {
        return ESecurityRole.ADMIN;
    }

    @Override
    public String getName() {
        return ADMIN_NAME;
    }
}
