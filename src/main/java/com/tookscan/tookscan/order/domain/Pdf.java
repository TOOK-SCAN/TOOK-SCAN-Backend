package com.tookscan.tookscan.order.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "pdfs")
@SQLDelete(sql = "UPDATE pdfs SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Pdf extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "pdf_created_at")
    private LocalDateTime pdfCreatedAt;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked;

    /* -------------------------------------------- */
    /* One To One Mapping ------------------------ */
    /* -------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Pdf(String pdfUrl, LocalDateTime pdfCreatedAt, boolean isChecked, Document document) {
        this.pdfUrl = pdfUrl;
        this.pdfCreatedAt = pdfCreatedAt;
        this.isChecked = isChecked;
        this.document = document;
    }
}
