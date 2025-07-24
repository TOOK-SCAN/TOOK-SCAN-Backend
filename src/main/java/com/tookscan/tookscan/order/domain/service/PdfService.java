package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.util.List;
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
     * Document에 속한 PDF 파일들의 파일명을 재정렬하는 메서드
     * PDF 삭제 후 파일명 순서를 다시 정렬합니다.
     *
     * @param document    재정렬할 PDF들이 속한 Document 객체
     * @param s3Renamer   S3에서 파일명을 변경하고 새 URL을 반환하는 함수 (document, oldUrl, newFileName) -> newUrl
     */
    public void reorderPdfFileNames(Document document, TriFunction<Document, String, String, String> s3Renamer) {
        List<Pdf> pdfs = document.getPdfs();
        if (pdfs.isEmpty()) {
            return;
        }

        String baseName = document.getName();
        String extension = ".pdf";

        for (int i = 0; i < pdfs.size(); i++) {
            Pdf pdf = pdfs.get(i);
            String oldUrl = pdf.getPdfUrl();
            
            // 새로운 파일명 생성
            String newFileName = i == 0 ? baseName + extension : baseName + " (" + i + ")" + extension;
            
            // 기존 파일명과 새 파일명이 다른 경우에만 URL 업데이트
            String currentFileName = extractFileNameFromUrl(oldUrl);
            if (!currentFileName.equals(newFileName)) {
                String newUrl = s3Renamer.apply(document, oldUrl, newFileName);
                updatePdfUrl(pdf, newUrl);
            }
        }
    }

    /**
     * 함수형 인터페이스: 3개의 매개변수를 받는 함수
     */
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    /**
     * URL에서 파일명을 추출하는 메서드
     */
    private String extractFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        return lastSlashIndex != -1 ? url.substring(lastSlashIndex + 1) : url;
    }
}
