package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import org.springframework.web.multipart.MultipartFile;

@UseCase
public interface UploadAdminDocumentsPdfUseCase {
    void execute(Long documentId, MultipartFile file);
}
