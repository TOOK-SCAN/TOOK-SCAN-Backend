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

        @JsonProperty("sales")
        private final Integer sales;

        @Builder
        public MonthlyStatisticsDto(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                    Integer signUpCount, Integer orderCount, Integer sales) {
            this.yearMonth = yearMonth;
            this.pageViewCount = pageViewCount;
            this.visitantCount = visitantCount;
            this.signUpCount = signUpCount;
            this.orderCount = orderCount;
            this.sales = sales;
        }

        public static MonthlyStatisticsDto of(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                              Integer signUpCount, Integer orderCount, Integer sales) {
            return MonthlyStatisticsDto.builder()
                    .yearMonth(yearMonth)
                    .pageViewCount(pageViewCount)
                    .visitantCount(visitantCount)
                    .signUpCount(signUpCount)
                    .orderCount(orderCount)
                    .sales(sales)
                    .build();
        }
    }
}
