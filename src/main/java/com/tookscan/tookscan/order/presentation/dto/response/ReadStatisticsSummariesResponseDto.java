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

        @JsonProperty("spring_average_page_count")
        private final Double springAveragePageCount;

        @JsonProperty("raw_average_page_count")
        private final Double rawAveragePageCount;

        @JsonProperty("discard_average_page_count")
        private final Double discardAveragePageCount;

        @JsonProperty("spring_average_book_price")
        private final Double springAverageBookPrice;

        @JsonProperty("raw_average_book_price")
        private final Double rawAverageBookPrice;

        @JsonProperty("discard_average_book_price")
        private final Double discardAverageBookPrice;

        @Builder
        public MonthlyStatisticsDto(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                    Integer signUpCount, Integer orderCount, Integer appliedCount,
                                    Integer arrivedCount, Integer completedCount, Integer discardedCount,
                                    Integer springCount, Integer rawCount,
                                    Double springAveragePageCount, Double rawAveragePageCount, Double discardAveragePageCount,
                                    Double springAverageBookPrice, Double rawAverageBookPrice, Double discardAverageBookPrice) {
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
            this.springAveragePageCount = springAveragePageCount != null ? Math.round(springAveragePageCount * 10.0) / 10.0 : 0.0;
            this.rawAveragePageCount = rawAveragePageCount != null ? Math.round(rawAveragePageCount * 10.0) / 10.0 : 0.0;
            this.discardAveragePageCount = discardAveragePageCount != null ? Math.round(discardAveragePageCount * 10.0) / 10.0 : 0.0;
            this.springAverageBookPrice = springAverageBookPrice != null ? Math.round(springAverageBookPrice * 10.0) / 10.0 : 0.0;
            this.rawAverageBookPrice = rawAverageBookPrice != null ? Math.round(rawAverageBookPrice * 10.0) / 10.0 : 0.0;
            this.discardAverageBookPrice = discardAverageBookPrice != null ? Math.round(discardAverageBookPrice * 10.0) / 10.0 : 0.0;
        }

        public static MonthlyStatisticsDto of(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                              Integer signUpCount, Integer orderCount, Integer appliedCount,
                                                Integer arrivedCount, Integer completedCount,
                                                Integer discardedCount, Integer springCount, Integer rawCount,
                                                Double springAveragePageCount, Double rawAveragePageCount, Double discardAveragePageCount,
                                                Double springAverageBookPrice, Double rawAverageBookPrice, Double discardAverageBookPrice) {
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
                    .springAveragePageCount(springAveragePageCount)
                    .rawAveragePageCount(rawAveragePageCount)
                    .discardAveragePageCount(discardAveragePageCount)
                    .springAverageBookPrice(springAverageBookPrice)
                    .rawAverageBookPrice(rawAverageBookPrice)
                    .discardAverageBookPrice(discardAverageBookPrice)
                    .build();
        }
    }
}
