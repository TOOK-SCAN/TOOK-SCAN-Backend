package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

@Repository
public interface PdfJpaRepository extends JpaRepository<Pdf, Long> {

    List<Pdf> findAllByDocumentId(Long orderId);
    
    @Query("SELECT p FROM Pdf p " +
           "JOIN p.document d " +
           "JOIN d.order o " +
           "WHERE o.pdfSendDate < :pdfSendDateBefore " +
           "AND p.expiredAt IS NULL")
    List<Pdf> findByOrderPdfSendDateBeforeAndExpiredAtIsNull(@Param("pdfSendDateBefore") LocalDateTime pdfSendDateBefore);

    @Query("SELECT p.name FROM Pdf p WHERE p.document.id = :documentId AND p.name IN :names")
    List<String> findExistingNames(@Param("documentId") Long documentId, @Param("names") Collection<String> names);
}
