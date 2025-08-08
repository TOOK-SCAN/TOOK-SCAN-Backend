package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.order.domain.Pdf;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UploadAdminDocumentsPdfResponseDto extends SelfValidating<UploadAdminDocumentsPdfResponseDto> {
    @JsonProperty("pdfs")
    @NotNull
    private final List<UploadAdminDocumentsPdfDto> pdfs;

    @Builder
    public UploadAdminDocumentsPdfResponseDto(List<UploadAdminDocumentsPdfDto> pdfs) {
        this.pdfs = pdfs;
        this.validateSelf();
    }

    public static UploadAdminDocumentsPdfResponseDto fromEntity(List<Pdf> pdfs) {
        return UploadAdminDocumentsPdfResponseDto.builder()
                .pdfs(pdfs.stream()
                        .map(UploadAdminDocumentsPdfDto::fromEntity)
                        .toList())
                .build();
    }

    @Getter
    public static class UploadAdminDocumentsPdfDto {
        @JsonProperty("id")
        @NotNull
        private final String id;

        @JsonProperty("name")
        @NotNull
        private final String name;

        @Builder
        public UploadAdminDocumentsPdfDto(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public static UploadAdminDocumentsPdfDto fromEntity(Pdf pdf) {
            return UploadAdminDocumentsPdfDto.builder()
                    .id(pdf.getId().toString())
                    .name(pdf.getName())
                    .build();
        }
    }

}
