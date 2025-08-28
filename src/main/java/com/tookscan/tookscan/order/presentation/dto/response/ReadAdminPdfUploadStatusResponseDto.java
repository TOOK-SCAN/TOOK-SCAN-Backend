package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAdminPdfUploadStatusResponseDto extends SelfValidating<ReadAdminPdfUploadStatusResponseDto> {

    @JsonProperty("upload_status")
    private final EPdfUploadStatus uploadStatus;

    @JsonProperty("pdf_url_for_admin")
    private final String pdfUrlForAdmin;

    @JsonProperty("name")
    private final String name;

    @Builder
    public ReadAdminPdfUploadStatusResponseDto(EPdfUploadStatus uploadStatus, String pdfUrlForAdmin, String name) {
        this.uploadStatus = uploadStatus;
        this.pdfUrlForAdmin = pdfUrlForAdmin;
        this.name = name;
        this.validateSelf();
    }

    public static ReadAdminPdfUploadStatusResponseDto fromEntity(Pdf pdf) {
        String pdfUrlForAdmin = null;
        
        // Only include pdf_url_for_admin if upload_status is COMPLETED and url exists
        if (pdf.getUploadStatus() == EPdfUploadStatus.COMPLETED && pdf.getPdfUrlForAdmin() != null) {
            pdfUrlForAdmin = pdf.getPdfUrlForAdmin();
        }

        return ReadAdminPdfUploadStatusResponseDto.builder()
                .uploadStatus(pdf.getUploadStatus())
                .pdfUrlForAdmin(pdfUrlForAdmin)
                .name(pdf.getName())
                .build();
    }
}