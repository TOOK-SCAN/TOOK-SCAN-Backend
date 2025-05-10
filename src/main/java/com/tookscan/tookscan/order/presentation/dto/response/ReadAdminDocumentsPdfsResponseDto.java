package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminDocumentsPdfsResponseDto extends SelfValidating<ReadAdminDocumentsPdfsResponseDto> {

    @JsonProperty("pdf_url")
    private final String pdfUrl;

    @Builder
    public ReadAdminDocumentsPdfsResponseDto(String pdfUrl) {
        this.pdfUrl = pdfUrl;
        this.validateSelf();
    }

    public static ReadAdminDocumentsPdfsResponseDto of(String pdfUrl) {
        return new ReadAdminDocumentsPdfsResponseDto(pdfUrl);
    }
}
