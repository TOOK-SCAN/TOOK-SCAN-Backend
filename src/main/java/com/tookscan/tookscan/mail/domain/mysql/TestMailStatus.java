package com.tookscan.tookscan.mail.domain.mysql;

import com.tookscan.tookscan.core.dto.BaseEntity;
import jakarta.persistence.Entity;
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
@Table(name = "test_mail_statuses")
@SQLDelete(sql = "UPDATE test_mail_statuses SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class TestMailStatus extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    private String email;

    private Boolean isSent;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public TestMailStatus(String email, Boolean isSent) {
        this.email = email;
        this.isSent = isSent;
    }
}
