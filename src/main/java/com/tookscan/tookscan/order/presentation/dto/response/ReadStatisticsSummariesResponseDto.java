package com.tookscan.tookscan.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tookscan.tookscan.core.dto.SelfValidating;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadStatisticsSummariesResponseDto extends SelfValidating<ReadStatisticsSummariesResponseDto> {
    @JsonProperty("today_visitant_count")
    private final Integer todayVisitantCount;

    @JsonProperty("today_page_view_count")
    private final Integer todayPageViewCount;

    @JsonProperty("statistics")
    private final List<MonthlyStatisticsDto> statistics;

    @Builder
    public ReadStatisticsSummariesResponseDto(Integer todayVisitantCount, Integer todayPageViewCount,
                                              List<MonthlyStatisticsDto> statistics) {
        this.todayVisitantCount = todayVisitantCount;
        this.todayPageViewCount = todayPageViewCount;
        this.statistics = statistics;
        this.validateSelf();
    }

    public static ReadStatisticsSummariesResponseDto from(List<MonthlyStatisticsDto> statisticsDto) {
        return ReadStatisticsSummariesResponseDto.builder()
                .todayVisitantCount(statisticsDto.stream().mapToInt(MonthlyStatisticsDto::getVisitantCount).sum())
                .todayPageViewCount(statisticsDto.stream().mapToInt(MonthlyStatisticsDto::getPageViewCount).sum())
                .statistics(statisticsDto)
                .build();
    }

    @Getter
    public static class MonthlyStatisticsDto {
        @JsonProperty("year_month")
        private final String yearMonth;

        @JsonProperty("page_view_count")
        private final Integer pageViewCount;

        @JsonProperty("visitant_count")
        private final Integer visitantCount;

        @JsonProperty("sign_up_count")
        private final Integer signUpCount;

        @JsonProperty("order_count")
        private final Integer orderCount;

        @JsonProperty("applied_count")
        private final Integer appliedCount;

        @JsonProperty("arrived_count")
        private final Integer arrivedCount;

        @JsonProperty("completed_count")
        private final Integer completedCount;

        @JsonProperty("discarded_count")
        private final Integer discardedCount;

        @JsonProperty("spring_count")
        private final Integer springCount;

        @JsonProperty("raw_count")
        private final Integer rawCount;

        @Builder
        public MonthlyStatisticsDto(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                    Integer signUpCount, Integer orderCount, Integer appliedCount,
                                    Integer arrivedCount, Integer completedCount, Integer discardedCount,
                                    Integer springCount, Integer rawCount) {
            this.yearMonth = yearMonth;
            this.pageViewCount = pageViewCount;
            this.visitantCount = visitantCount;
            this.signUpCount = signUpCount;
            this.orderCount = orderCount;
            this.appliedCount = appliedCount;
            this.arrivedCount = arrivedCount;
            this.completedCount = completedCount;
            this.discardedCount = discardedCount;
            this.springCount = springCount;
            this.rawCount = rawCount;
        }

        public static MonthlyStatisticsDto of(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                              Integer signUpCount, Integer orderCount, Integer appliedCount,
                                                Integer arrivedCount, Integer completedCount,
                                                Integer discardedCount, Integer springCount, Integer rawCount) {
            return MonthlyStatisticsDto.builder()
                    .yearMonth(yearMonth)
                    .pageViewCount(pageViewCount)
                    .visitantCount(visitantCount)
                    .signUpCount(signUpCount)
                    .orderCount(orderCount)
                    .appliedCount(appliedCount)
                    .arrivedCount(arrivedCount)
                    .completedCount(completedCount)
                    .discardedCount(discardedCount)
                    .springCount(springCount)
                    .rawCount(rawCount)
                    .build();
        }
    }
}
