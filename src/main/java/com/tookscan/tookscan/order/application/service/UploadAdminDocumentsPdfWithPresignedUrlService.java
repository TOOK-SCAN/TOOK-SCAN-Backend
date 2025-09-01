package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfWithPresignedUrlUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.service.PdfService;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import com.tookscan.tookscan.order.presentation.dto.request.UploadAdminDocumentsPdfWithPresignedUrlRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.UploadAdminDocumentsPdfWithPresignedUrlResponseDto;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfWithPresignedUrlService implements UploadAdminDocumentsPdfWithPresignedUrlUseCase {

    private final PdfRepository pdfRepository;
    private final DocumentRepository documentRepository;
    private final PdfService pdfService;
    private final S3Util s3Util;
    private final static Integer PRESIGNED_URL_EXPIRATION_MINUTES = 15;

    @Override
    @Transactional
    @BusinessLog(
            domain = "Order",
            action = "upload pdfs with presigned urls",
            userType = "ADMIN"
    )
    public UploadAdminDocumentsPdfWithPresignedUrlResponseDto execute(Long documentId,
                                                                      UploadAdminDocumentsPdfWithPresignedUrlRequestDto requestDto) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);

        // 요청 파일명 중복 검증 및 기존 파일명과의 중복 검증
        Set<String> uniqueNames = new HashSet<>();
        for (String name : requestDto.fileNames()) {
            if (name == null || name.isBlank()) {
                throw new CommonException(ErrorCode.INVALID_ARGUMENT, "파일명이 비어있습니다.");
            }
            if (!uniqueNames.add(name)) {
                throw new CommonException(ErrorCode.DUPLICATE_PDF_FILENAME, "중복된 파일명이 감지되었습니다: " + name);
            }
        }
        pdfService.validateUniqueFilenames(document, uniqueNames);

        List<Pdf> toSave = new ArrayList<>();
        List<S3Util.PresignedUpload> presignedUploads = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        for (String originalFileName : requestDto.fileNames()) {

            String storedFileName = UUID.randomUUID() + ".pdf";

            Pdf preCreated = Pdf.builder()
                    .name(originalFileName)
                    .storedFileName(storedFileName)
                    .isChecked(false)
                    .document(document)
                    .uploadStatus(EPdfUploadStatus.IN_PROGRESS)
                    .build();
            toSave.add(preCreated);

            S3Util.PresignedUpload presigned = s3Util.getPresignedPutUrlForPdf(
                    document,
                    storedFileName,
                    originalFileName,
                    Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES)
            );
            presignedUploads.add(presigned);
            fileNames.add(originalFileName);
        }

        // PDF 저장
        pdfRepository.saveAll(toSave);

        // 저장된 PDF ID와 함께 UploadInfo 생성
        List<UploadAdminDocumentsPdfWithPresignedUrlResponseDto.UploadInfo> uploads = new ArrayList<>();
        for (int i = 0; i < toSave.size(); i++) {
            uploads.add(UploadAdminDocumentsPdfWithPresignedUrlResponseDto.UploadInfo.builder()
                    .pdfId(toSave.get(i).getId().toString())
                    .fileName(fileNames.get(i))
                    .url(presignedUploads.get(i).getUrl())
                    .headers(presignedUploads.get(i).getHeaders())
                    .build());
        }

        return UploadAdminDocumentsPdfWithPresignedUrlResponseDto.builder()
                .uploads(uploads)
                .build();
    }
}
