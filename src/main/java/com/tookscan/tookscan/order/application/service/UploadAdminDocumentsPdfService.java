package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    private final DocumentRepository documentRepository;

    private final S3Util s3Util;

    @Override
    public void execute(Long documentId, MultipartFile file) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        s3Util.uploadPdf(document, file);
    }
}
