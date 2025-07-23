package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@UseCase
public interface UploadAdminDocumentsPdfUseCase {
    void execute(Long documentId, List<MultipartFile> files);
}
