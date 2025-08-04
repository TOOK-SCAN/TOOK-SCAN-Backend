package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminDocumentsPdfsResponseDto extends SelfValidating<ReadAdminDocumentsPdfsResponseDto> {

    @JsonProperty("pdf_urls")
    private final List<String> pdfUrls;

    @Builder
    public ReadAdminDocumentsPdfsResponseDto(List<String> pdfUrls) {
        this.pdfUrls = pdfUrls;
        this.validateSelf();
    }

    public static ReadAdminDocumentsPdfsResponseDto fromEntity(Document document) {
        List<String> pdfUrls = document.getPdfs().stream()
                .map(Pdf::getPdfUrlForAdmin)
                .toList();

        return ReadAdminDocumentsPdfsResponseDto.builder()
                .pdfUrls(pdfUrls)
                .build();
    }
}
