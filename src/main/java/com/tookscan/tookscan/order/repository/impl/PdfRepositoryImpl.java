package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.PdfRepository;
import com.tookscan.tookscan.order.repository.mysql.PdfJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PdfRepositoryImpl implements PdfRepository {
    private final PdfJpaRepository pdfJpaRepository;

    @Override
    public List<Pdf> findAllByDocumentId(Long documentId) {
        return pdfJpaRepository.findAllByDocumentId(documentId);
    }

    @Override
    public void save(Pdf pdf) {
        pdfJpaRepository.save(pdf);
    }

    @Override
    public void deleteByIdOrElseThrow(Long id) {
        pdfJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PDF_FILE, "문서 ID: " + id));
        pdfJpaRepository.deleteById(id);
    }
}
