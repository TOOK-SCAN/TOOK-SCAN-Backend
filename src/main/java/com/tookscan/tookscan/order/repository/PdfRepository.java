package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.Pdf;

import java.util.List;

public interface PdfRepository {

    List<Pdf> findAllByDocumentId(Long documentId);
    void save(Pdf pdf);
}
