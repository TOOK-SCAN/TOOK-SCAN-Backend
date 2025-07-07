package com.tookscan.tookscan.notice.domain;

import com.tookscan.tookscan.core.dto.BaseEntity;
import jakarta.persistence.Column;
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
@Table(name = "notices")
@SQLDelete(sql = "UPDATE notices SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Notice extends BaseEntity {

    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public Notice(
            String title,
            String content,
            Boolean isPublic
    ) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.viewCount = 0L;
    }

    /**
     * 제목을 업데이트합니다.
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * 내용을 업데이트합니다.
     */
    public void updateContent(String content) {
        this.content = content;
    }

    /**
     * 공개 여부를 업데이트합니다.
     */
    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * 조회수를 증가시킵니다.
     */
    public void increaseViewCount() {
        this.viewCount++;
    }
} 