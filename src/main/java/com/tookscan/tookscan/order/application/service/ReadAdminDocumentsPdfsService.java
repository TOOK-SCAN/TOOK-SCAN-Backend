package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminDocumentsPdfsResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminDocumentsPdfsUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminDocumentsPdfsService implements ReadAdminDocumentsPdfsUseCase {

    private final DocumentRepository documentRepository;

    private final S3Util s3Util;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminDocumentsPdfsResponseDto execute(Long documentId) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);
        return ReadAdminDocumentsPdfsResponseDto.of(s3Util.generateSignedUrl(document));
    }
}
