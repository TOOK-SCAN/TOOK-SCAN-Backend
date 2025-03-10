package com.tookscan.tookscan.order.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateAdminOrderCouponRequestDto(
        @JsonProperty("name")
        String name,

        @JsonProperty("description")
        String description,

        @JsonProperty("type")
        ECouponType type,

        @JsonProperty("is_possible_duplicated_apply")
        Boolean isPossibleDuplicatedApply,

        @JsonProperty("count")
        @Min(1)
        Integer count,

        @JsonProperty("tag")
        @Size(min = 2, max = 5)
        String tag,

        @JsonProperty("discount_price")
        Integer discountPrice,

        @JsonProperty("discount_percent")
        @Min(1)
        @Max(100)
        Integer discountPercent,

        @JsonProperty("start_date_time")
        LocalDateTime startDateTime,

        @JsonProperty("end_date_time")
        LocalDateTime endDateTime
) {
}
