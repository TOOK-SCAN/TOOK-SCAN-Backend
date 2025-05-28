package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    private final DocumentRepository documentRepository;
    private final PdfRepository pdfRepository;

    private final S3Util s3Util;

    @Override
    public void execute(Long documentId, MultipartFile file) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        String pdfUrl = s3Util.uploadPdf(document, file);

        Pdf pdf = Pdf.builder()
                .pdfUrl(pdfUrl)
                .pdfCreatedAt(LocalDateTime.now())
                .document(document)
                .build();

        pdfRepository.save(pdf);
    }
}
