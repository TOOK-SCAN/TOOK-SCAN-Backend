package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.PdfRepository;
import com.tookscan.tookscan.order.repository.mysql.PdfJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PdfRepositoryImpl implements PdfRepository {
    private final PdfJpaRepository pdfJpaRepository;

    @Override
    public void save(Pdf pdf) {
        pdfJpaRepository.save(pdf);
    }
}
