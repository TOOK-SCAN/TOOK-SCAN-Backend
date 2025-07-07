package com.tookscan.tookscan.notice.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateAdminNoticeRequestDto extends SelfValidating<UpdateAdminNoticeRequestDto> {

    @JsonProperty("title")
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 30, message = "제목은 30자 이내로 입력해주세요.")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @JsonProperty("is_public")
    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean isPublic;

    @Builder
    public UpdateAdminNoticeRequestDto(String title, String content, Boolean isPublic) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.validateSelf();
    }
} 