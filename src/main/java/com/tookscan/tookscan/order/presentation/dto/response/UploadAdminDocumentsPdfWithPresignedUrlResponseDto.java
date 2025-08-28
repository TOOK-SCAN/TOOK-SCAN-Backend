package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UploadAdminDocumentsPdfWithPresignedUrlResponseDto extends
        SelfValidating<UploadAdminDocumentsPdfWithPresignedUrlResponseDto> {
    @JsonProperty("uploads")
    private final List<UploadInfo> uploads;

    @Builder
    public UploadAdminDocumentsPdfWithPresignedUrlResponseDto(List<UploadInfo> uploads) {
        this.uploads = uploads;
        this.validateSelf();
    }

    @Getter
    public static class UploadInfo extends SelfValidating<UploadInfo> {
        @JsonProperty("pdf_id")
        private String pdfId;

        @JsonProperty("file_name")
        private final String fileName;

        @JsonProperty("url")
        private final String url;

        @JsonProperty("headers")
        private final Map<String, String> headers;

        @Builder
        public UploadInfo(String pdfId, String fileName, String url, Map<String, String> headers) {
            this.pdfId = pdfId;
            this.fileName = fileName;
            this.url = url;
            this.headers = headers;
            this.validateSelf();
        }
    }
}
