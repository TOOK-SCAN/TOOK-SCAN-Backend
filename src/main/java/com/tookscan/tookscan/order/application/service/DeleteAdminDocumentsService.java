package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.DeleteAdminDocumentsUseCase;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAdminDocumentsService implements DeleteAdminDocumentsUseCase {
    private final DocumentRepository documentRepository;

    @Override
    public void execute(List<Long> documentIds) {
        documentIds.forEach(documentRepository::deleteByIdOrElseThrow);
    }
}
