package com.tookscan.tookscan.order.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateAdminOrderCouponRequestDto(
        @JsonProperty("name")
        @NotBlank(message = "쿠폰 이름을 입력해주세요.")
        String name,

        @JsonProperty("description")
        @NotBlank(message = "쿠폰 설명을 입력해주세요.")
        String description,

        @JsonProperty("type")
        @NotNull(message = "쿠폰 타입을 입력해주세요.")
        ECouponType type,

        @JsonProperty("is_possible_duplicated_apply")
        @NotNull(message = "중복 적용 여부를 입력해주세요.")
        Boolean isPossibleDuplicatedApply,

        @JsonProperty("is_user_only")
        @NotNull(message = "회원 전용 여부를 입력해주세요.")
        Boolean isUserOnly,

        @JsonProperty("count")
        @Min(1)
        Integer count,

        @JsonProperty("tag")
        @NotNull(message = "태그를 입력해주세요.")
        @Size(min = 2, max = 5)
        String tag,

        @JsonProperty("discount_price")
        Integer discountPrice,

        @JsonProperty("discount_percent")
        @Min(1)
        @Max(100)
        Integer discountPercent,

        @JsonProperty("start_date_time")
        @NotNull(message = "시작 일시를 입력해주세요.")
        LocalDateTime startDateTime,

        @JsonProperty("end_date_time")
        @NotNull(message = "종료 일시를 입력해주세요.")
        LocalDateTime endDateTime
) {
}
