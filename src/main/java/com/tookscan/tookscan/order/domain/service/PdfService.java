package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.PdfRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final PdfRepository pdfRepository;

    /**
     * Pdf 엔티티의 URL을 업데이트하는 메서드
     *
     * @param pdf    업데이트할 Pdf 객체
     * @param newUrl 새로운 URL
     */
    public void updatePdfUrlForAdmin(Pdf pdf, String newUrl) {
        pdf.updatePdfUrlForAdmin(newUrl);
    }

    /**
     * Document 내에서 파일명 중복을 확인하는 메서드 중복된 파일명이 있으면 예외를 발생시킵니다.
     *
     * @param document Document 객체
     * @param fileName 원본 파일명
     * @throws CommonException 중복된 파일명이 존재할 경우
     */
    public void validateUniqueFilename(Document document, String fileName) {
        // 현재 Document에 속한 모든 PDF의 원본 파일명 수집
        Set<String> existingFilenames = document.getPdfs().stream()
                .map(Pdf::getName)
                .collect(Collectors.toSet());

        // 파일명이 중복되면 예외 발생
        if (existingFilenames.contains(fileName)) {
            throw new CommonException(ErrorCode.DUPLICATE_PDF_FILENAME, "파일명: " + fileName);
        }
    }

    /**
     * Document 내에서 다수의 파일명 중복을 한 번에 확인합니다.
     * 하나라도 중복되면 예외를 던집니다.
     *
     * @param document  Document 객체
     * @param fileNames 요청에서 전달된 파일명 집합
     * @throws CommonException 중복된 파일명이 하나라도 존재할 경우
     */
    public void validateUniqueFilenames(Document document, Iterable<String> fileNames) {
        Set<String> requested = StreamSupport.stream(fileNames.spliterator(), false)
                .collect(Collectors.toSet());

        if (requested.isEmpty()) {
            return;
        }

        List<String> duplicates = pdfRepository.findExistingNames(document.getId(), requested);
        if (!duplicates.isEmpty()) {
            String duplicateFileNames = String.join(", ", duplicates);
            throw new CommonException(ErrorCode.DUPLICATE_PDF_FILENAME,
                    "기존 문서와 중복된 파일명이 포함되어 있습니다. 파일명: " + duplicateFileNames);
        }
    }
}
