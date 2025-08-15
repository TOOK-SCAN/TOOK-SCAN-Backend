package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.Pdf;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

public interface PdfRepository {

    List<Pdf> findAllByDocumentId(Long documentId);
    Pdf save(Pdf pdf);

    void deleteByIdOrElseThrow(Long pdfId);

    Pdf findByIdOrElseThrow(Long pdfId);
    
    List<Pdf> findPdfsByOrderPdfSendDateBefore(LocalDateTime pdfSendDateBefore);
    
    void saveAll(List<Pdf> pdfs);

    List<String> findExistingNames(Long documentId, Collection<String> names);
}
