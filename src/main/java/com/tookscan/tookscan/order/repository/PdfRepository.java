package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.Pdf;
import java.time.LocalDateTime;
import java.util.List;

public interface PdfRepository {

    List<Pdf> findAllByDocumentId(Long documentId);
    void save(Pdf pdf);

    void deleteByIdOrElseThrow(Long pdfId);

    Pdf findByIdOrElseThrow(Long pdfId);
    
    List<Pdf> findPdfsByOrderPdfSendDateBefore(LocalDateTime pdfSendDateBefore);
    
    void saveAll(List<Pdf> pdfs);
}
