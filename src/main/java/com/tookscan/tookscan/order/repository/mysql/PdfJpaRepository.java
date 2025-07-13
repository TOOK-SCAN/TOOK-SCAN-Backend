package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfJpaRepository extends JpaRepository<Pdf, Long> {

    List<Pdf> findAllByDocumentId(Long orderId);
}
