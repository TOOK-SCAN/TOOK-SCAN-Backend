package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.presentation.dto.request.DeleteAdminDocumentsRequestDto;
import com.tookscan.tookscan.order.application.usecase.DeleteAdminDocumentsUseCase;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAdminDocumentsService implements DeleteAdminDocumentsUseCase {
    private final DocumentRepository documentRepository;

    @Override
    @Transactional
    public void execute(DeleteAdminDocumentsRequestDto requestDto) {
        requestDto.documentIds().forEach(documentRepository::deleteByIdOrElseThrow);
    }
}
