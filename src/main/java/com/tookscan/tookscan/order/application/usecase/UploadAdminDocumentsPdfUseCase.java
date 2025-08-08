package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.order.presentation.dto.response.UploadAdminDocumentsPdfResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@UseCase
public interface UploadAdminDocumentsPdfUseCase {
    UploadAdminDocumentsPdfResponseDto execute(Long documentId, List<MultipartFile> files);
}
