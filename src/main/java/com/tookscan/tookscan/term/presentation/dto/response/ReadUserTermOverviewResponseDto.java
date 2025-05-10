package com.tookscan.tookscan.term.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import com.tookscan.tookscan.term.domain.Term;
import com.tookscan.tookscan.term.domain.type.ETermType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserTermOverviewResponseDto extends SelfValidating<ReadUserTermOverviewResponseDto> {

    @JsonProperty("terms")
    private final List<TermInfoDto> terms;

    @Builder
    public ReadUserTermOverviewResponseDto(List<TermInfoDto> terms) {
        this.terms = terms;
        this.validateSelf();
    }

    public static class TermInfoDto extends SelfValidating<TermInfoDto> {

        @JsonProperty("id")
        @NotNull(message = "id는 null이 될 수 없습니다.")
        private final String id;

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

        @Builder
        public TermInfoDto(String id, ETermType type, String title, String content, Boolean isRequired,
                           Boolean isVisible) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.content = content;
            this.isRequired = isRequired;
            this.isVisible = isVisible;
            this.validateSelf();
        }

        public static ReadUserTermOverviewResponseDto.TermInfoDto fromEntity(Term term) {
            return ReadUserTermOverviewResponseDto.TermInfoDto.builder()
                    .id(term.getId().toString())
                    .type(term.getType())
                    .title(term.getTitle())
                    .content(term.getContent())
                    .isRequired(term.getIsRequired())
                    .isVisible(term.getIsVisible())
                    .build();
        }
    }

    public static ReadUserTermOverviewResponseDto fromEntities(List<Term> terms) {
        return ReadUserTermOverviewResponseDto.builder()
                .terms(terms.stream()
                        .map(TermInfoDto::fromEntity)
                        .toList()
                ).build();
    }
}
