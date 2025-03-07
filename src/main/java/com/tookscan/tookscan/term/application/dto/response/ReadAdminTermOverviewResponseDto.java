package com.tookscan.tookscan.term.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.type.ETermType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReadAdminTermOverviewResponseDto extends SelfValidating<ReadAdminTermOverviewResponseDto> {

    @JsonProperty("terms")
    private final List<TermInfoDto> terms;

    @Builder
    public ReadAdminTermOverviewResponseDto(List<TermInfoDto> terms) {
        this.terms = terms;
        this.validateSelf();
    }

    public static class TermInfoDto extends SelfValidating<TermInfoDto> {

        @JsonProperty("id")
        @NotNull(message = "id는 null이 될 수 없습니다.")
        @Min(value = 1, message = "id는 1 이상이어야 합니다.")
        private final Long id;

        @JsonProperty("type")
        @NotNull(message = "type은 null이 될 수 없습니다.")
        private final ETermType type;

        @JsonProperty("title")
        @NotBlank(message = "title은 null이 될 수 없습니다.")
        private final String title;

        @JsonProperty("content")
        @NotBlank(message = "content는 null이 될 수 없습니다.")
        private final String content;

        @JsonProperty("is_required")
        @NotNull(message = "is_required는 null이 될 수 없습니다.")
        private final Boolean isRequired;

        @JsonProperty("is_visible")
        @NotNull(message = "is_visible는 null이 될 수 없습니다.")
        private final Boolean isVisible;

        @JsonProperty("sort_order")
        @NotNull(message = "sort_order는 null이 될 수 없습니다.")
        private final Integer sortOrder;

        @Builder
        public TermInfoDto(Long id, ETermType type, String title, String content, Boolean isRequired, Boolean isVisible, Integer sortOrder) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.content = content;
            this.isRequired = isRequired;
            this.isVisible = isVisible;
            this.sortOrder = sortOrder;
            this.validateSelf();
        }

        public static TermInfoDto fromEntity(Term term) {
            return TermInfoDto.builder()
                    .id(term.getId())
                    .type(term.getType())
                    .title(term.getTitle())
                    .content(term.getContent())
                    .isRequired(term.getIsRequired())
                    .isVisible(term.getIsVisible())
                    .sortOrder(term.getSortOrder())
                    .build();
        }
    }

    public static ReadAdminTermOverviewResponseDto fromEntities(List<Term> terms) {
        return ReadAdminTermOverviewResponseDto.builder()
                .terms(terms.stream()
                        .map(TermInfoDto::fromEntity)
                        .toList()
                ).build();
    }

}
