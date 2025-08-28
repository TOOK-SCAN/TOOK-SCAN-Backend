package com.tookscan.tookscan.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UploadAdminDocumentsPdfWithPresignedUrlRequestDto(
        @JsonProperty("file_names")
        List<String> fileNames
) {
}
