package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.order.repository.PdfRepository;
import com.tookscan.tookscan.order.repository.mysql.PdfJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PdfRepositoryImpl implements PdfRepository {
    private final PdfJpaRepository pdfJpaRepository;
}
