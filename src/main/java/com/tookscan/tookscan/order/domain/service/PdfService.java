package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfService {

    /**
     * Pdf 엔티티의 URL을 업데이트하는 메서드
     *
     * @param pdf    업데이트할 Pdf 객체
     * @param newUrl 새로운 URL
     */
    public void updatePdfUrl(Pdf pdf, String newUrl) {
        pdf.updatePdfUrl(newUrl);
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
}
