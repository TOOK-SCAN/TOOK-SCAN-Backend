package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "pdfs")
@SQLDelete(sql = "UPDATE pdfs SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Pdf extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "pdf_url_for_admin", nullable = false, length = 2048)
    private String pdfUrlForAdmin;

    @Column(name = "pdf_url_for_user", length = 2048)
    private String pdfUrlForUser;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    /* -------------------------------------------- */
    /* Many To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Pdf(String pdfUrlForAdmin, String name, boolean isChecked, Document document, String storedFileName) {
        this.pdfUrlForAdmin = pdfUrlForAdmin;
        this.name = name;
        this.isChecked = isChecked;
        this.document = document;
        this.storedFileName = storedFileName;
    }
    public void updateExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void updatePdfUrlForAdmin(String pdfUrlForAdmin) {
        this.pdfUrlForAdmin = pdfUrlForAdmin;
    }

    public void updatePdfUrlForUser(String pdfUrlForUser) {
        this.pdfUrlForUser = pdfUrlForUser;
    }

}
