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

    @JsonProperty("total_statistics")
    private final TotalStatisticsDto totalStatistics;

    @Builder
    public ReadStatisticsSummariesResponseDto(Integer todayVisitantCount, Integer todayPageViewCount,
                                              List<MonthlyStatisticsDto> statistics, TotalStatisticsDto totalStatistics) {
        this.todayVisitantCount = todayVisitantCount;
        this.todayPageViewCount = todayPageViewCount;
        this.statistics = statistics;
        this.totalStatistics = totalStatistics != null ? totalStatistics : calculateTotalStatistics(statistics);
        this.validateSelf();
    }

    public static ReadStatisticsSummariesResponseDto from(List<MonthlyStatisticsDto> statisticsDto) {
        return ReadStatisticsSummariesResponseDto.builder()
                .todayVisitantCount(statisticsDto.stream().mapToInt(MonthlyStatisticsDto::getVisitantCount).sum())
                .todayPageViewCount(statisticsDto.stream().mapToInt(MonthlyStatisticsDto::getPageViewCount).sum())
                .statistics(statisticsDto)
                .totalStatistics(calculateTotalStatistics(statisticsDto))
                .build();
    }

    /**
     * 모든 월별 통계의 총 평균을 계산
     */
    private static TotalStatisticsDto calculateTotalStatistics(List<MonthlyStatisticsDto> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return TotalStatisticsDto.builder()
                    .discardedCount(0)
                    .springCount(0)
                    .rawCount(0)
                    .springAveragePageCount(0.0)
                    .rawAveragePageCount(0.0)
                    .discardAveragePageCount(0.0)
                    .springAverageDocumentPrice(0.0)
                    .rawAverageDocumentPrice(0.0)
                    .discardAverageDocumentPrice(0.0)
                    .build();
        }

        // 총합 계산
        Integer totalDiscardedCount = statistics.stream().mapToInt(MonthlyStatisticsDto::getDiscardedCount).sum();
        Integer totalSpringCount = statistics.stream().mapToInt(MonthlyStatisticsDto::getSpringCount).sum();
        Integer totalRawCount = statistics.stream().mapToInt(MonthlyStatisticsDto::getRawCount).sum();

        // 평균 계산 (모든 달에 대한 평균)
        Double avgSpringPageCount = statistics.stream()
                .mapToDouble(MonthlyStatisticsDto::getSpringAveragePageCount)
                .average()
                .orElse(0.0);

        Double avgRawPageCount = statistics.stream()
                .mapToDouble(MonthlyStatisticsDto::getRawAveragePageCount)
                .average()
                .orElse(0.0);

        Double avgDiscardPageCount = statistics.stream()
                .mapToDouble(MonthlyStatisticsDto::getDiscardAveragePageCount)
                .average()
                .orElse(0.0);

        Double avgSpringDocumentPrice = statistics.stream()
                .mapToDouble(MonthlyStatisticsDto::getSpringAverageDocumentPrice)
                .average()
                .orElse(0.0);

        Double avgRawDocumentPrice = statistics.stream()
                .mapToDouble(MonthlyStatisticsDto::getRawAverageDocumentPrice)
                .average()
                .orElse(0.0);

        Double avgDiscardDocumentPrice = statistics.stream()
                .mapToDouble(MonthlyStatisticsDto::getDiscardAverageDocumentPrice)
                .average()
                .orElse(0.0);

        return TotalStatisticsDto.builder()
                .discardedCount(totalDiscardedCount)
                .springCount(totalSpringCount)
                .rawCount(totalRawCount)
                .springAveragePageCount(avgSpringPageCount)
                .rawAveragePageCount(avgRawPageCount)
                .discardAveragePageCount(avgDiscardPageCount)
                .springAverageDocumentPrice(avgSpringDocumentPrice)
                .rawAverageDocumentPrice(avgRawDocumentPrice)
                .discardAverageDocumentPrice(avgDiscardDocumentPrice)
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

        @JsonProperty("spring_average_document_price")
        private final Double springAverageDocumentPrice;

        @JsonProperty("raw_average_document_price")
        private final Double rawAverageDocumentPrice;

        @JsonProperty("discard_average_document_price")
        private final Double discardAverageDocumentPrice;

        @Builder
        public MonthlyStatisticsDto(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                    Integer signUpCount, Integer orderCount, Integer appliedCount,
                                    Integer arrivedCount, Integer completedCount, Integer discardedCount,
                                    Integer springCount, Integer rawCount,
                                    Double springAveragePageCount, Double rawAveragePageCount, Double discardAveragePageCount,
                                    Double springAverageDocumentPrice, Double rawAverageDocumentPrice,
                                    Double discardAverageDocumentPrice) {
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
            this.springAverageDocumentPrice =
                    springAverageDocumentPrice != null ? Math.round(springAverageDocumentPrice * 10.0) / 10.0 : 0.0;
            this.rawAverageDocumentPrice =
                    rawAverageDocumentPrice != null ? Math.round(rawAverageDocumentPrice * 10.0) / 10.0 : 0.0;
            this.discardAverageDocumentPrice =
                    discardAverageDocumentPrice != null ? Math.round(discardAverageDocumentPrice * 10.0) / 10.0 : 0.0;
        }

        public static MonthlyStatisticsDto of(String yearMonth, Integer pageViewCount, Integer visitantCount,
                                              Integer signUpCount, Integer orderCount, Integer appliedCount,
                                                Integer arrivedCount, Integer completedCount,
                                                Integer discardedCount, Integer springCount, Integer rawCount,
                                                Double springAveragePageCount, Double rawAveragePageCount, Double discardAveragePageCount,
                                              Double springAverageDocumentPrice, Double rawAverageDocumentPrice,
                                              Double discardAverageDocumentPrice) {
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
                    .springAverageDocumentPrice(springAverageDocumentPrice)
                    .rawAverageDocumentPrice(rawAverageDocumentPrice)
                    .discardAverageDocumentPrice(discardAverageDocumentPrice)
                    .build();
        }
    }

    @Getter
    public static class TotalStatisticsDto {
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

        @JsonProperty("spring_average_document_price")
        private final Double springAverageDocumentPrice;

        @JsonProperty("raw_average_document_price")
        private final Double rawAverageDocumentPrice;

        @JsonProperty("discard_average_document_price")
        private final Double discardAverageDocumentPrice;

        @Builder
        public TotalStatisticsDto(Integer discardedCount, Integer springCount, Integer rawCount,
                                  Double springAveragePageCount, Double rawAveragePageCount,
                                  Double discardAveragePageCount,
                                  Double springAverageDocumentPrice, Double rawAverageDocumentPrice,
                                  Double discardAverageDocumentPrice) {
            this.discardedCount = discardedCount;
            this.springCount = springCount;
            this.rawCount = rawCount;
            this.springAveragePageCount =
                    springAveragePageCount != null ? Math.round(springAveragePageCount * 10.0) / 10.0 : 0.0;
            this.rawAveragePageCount =
                    rawAveragePageCount != null ? Math.round(rawAveragePageCount * 10.0) / 10.0 : 0.0;
            this.discardAveragePageCount =
                    discardAveragePageCount != null ? Math.round(discardAveragePageCount * 10.0) / 10.0 : 0.0;
            this.springAverageDocumentPrice =
                    springAverageDocumentPrice != null ? Math.round(springAverageDocumentPrice * 10.0) / 10.0 : 0.0;
            this.rawAverageDocumentPrice =
                    rawAverageDocumentPrice != null ? Math.round(rawAverageDocumentPrice * 10.0) / 10.0 : 0.0;
            this.discardAverageDocumentPrice =
                    discardAverageDocumentPrice != null ? Math.round(discardAverageDocumentPrice * 10.0) / 10.0 : 0.0;
        }
    }

}
