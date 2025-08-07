package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.application.usecase.ReadStatisticsSummariesUseCase;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.presentation.dto.response.ReadStatisticsSummariesResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadStatisticsSummariesResponseDto.MonthlyStatisticsDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadStatisticsSummariesService implements ReadStatisticsSummariesUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public ReadStatisticsSummariesResponseDto execute(
            String startYearMonth,
            String endYearMonth,
            Boolean isApplied,
            Boolean isArrived,
            Boolean isCompleted
    ) {
        // null인 경우 최근 6개월 기본값 설정
        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end;
        
        if (startYearMonth == null || endYearMonth == null) {
            end = now.withDayOfMonth(1);
            start = end.minusMonths(5); // 6개월 (현재 월 포함)
        } else {
            start = LocalDate.parse(startYearMonth + "-01");
            end = LocalDate.parse(endYearMonth + "-01");
        }

        // 1. 트랜잭션 내에서 모든 DB 조회 결과만 수집
        List<MonthlyStatisticsDto> statistics = loadStatistics(start, end, isApplied, isArrived, isCompleted);

        // 2. 트랜잭션 밖에서 정렬 처리
        statistics.sort((a, b) -> b.getYearMonth().compareTo(a.getYearMonth()));

        // 3. 최종 응답 객체 구성
        return ReadStatisticsSummariesResponseDto.from(statistics);
    }

    @Transactional(readOnly = true)
    protected List<MonthlyStatisticsDto> loadStatistics(LocalDate start, LocalDate end, Boolean isApplied, Boolean isArrived, Boolean isCompleted) {
        LocalDateTime periodStart = start.atStartOfDay();
        LocalDateTime periodEnd = end.plusMonths(1).atStartOfDay();
        
        // 배치 쿼리로 전체 기간의 데이터를 한 번에 조회
        Map<String, Integer> signUpCounts = userRepository.findMonthlySignUpCounts(periodStart, periodEnd);
        Map<String, Integer> orderCounts = orderRepository.findMonthlyOrderCounts(periodStart, periodEnd);
        Map<String, Integer> paymentAmounts = paymentRepository.findMonthlyPaymentAmounts(periodStart, periodEnd);
        Map<String, Map<EOrderStatus, Integer>> orderStatusCounts = orderRepository.findMonthlyOrderStatusCounts(periodStart, periodEnd);
        
        // 필터링된 월별 복구 옵션별 통계
        Map<String, Map<ERecoveryOption, Integer>> recoveryOptionCounts = orderRepository.findMonthlyRecoveryOptionCounts(periodStart, periodEnd, isApplied, isArrived, isCompleted);
        Map<String, Map<ERecoveryOption, Double>> recoveryOptionAveragePageCounts = orderRepository.findMonthlyRecoveryOptionAveragePageCounts(periodStart, periodEnd, isApplied, isArrived, isCompleted);
        Map<String, Map<ERecoveryOption, Double>> recoveryOptionAverageDocumentPrices = orderRepository.findMonthlyRecoveryOptionAverageDocumentPrices(periodStart, periodEnd, isApplied, isArrived, isCompleted);
        
        List<MonthlyStatisticsDto> result = new ArrayList<>();
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            String yearMonth = String.format("%04d-%02d", current.getYear(), current.getMonthValue());
            
            Integer signUpCount = signUpCounts.getOrDefault(yearMonth, 0);
            Integer orderCount = orderCounts.getOrDefault(yearMonth, 0);
            Integer totalAmount = paymentAmounts.getOrDefault(yearMonth, 0);
            
            // 주문 상태별 통계
            Map<EOrderStatus, Integer> statusMap = orderStatusCounts.getOrDefault(yearMonth, Map.of());
            Integer appliedCount = statusMap.getOrDefault(EOrderStatus.APPLY_COMPLETED, 0);
            Integer arrivedCount = statusMap.getOrDefault(EOrderStatus.COMPANY_ARRIVED, 0);
            Integer completedCount = statusMap.getOrDefault(EOrderStatus.ALL_COMPLETED, 0);
            
            // 복구 옵션별 통계
            Map<ERecoveryOption, Integer> recoveryMap = recoveryOptionCounts.getOrDefault(yearMonth, Map.of());
            Integer discardedCount = recoveryMap.getOrDefault(ERecoveryOption.DISCARD, 0);
            Integer springCount = recoveryMap.getOrDefault(ERecoveryOption.SPRING, 0);
            Integer rawCount = recoveryMap.getOrDefault(ERecoveryOption.RAW, 0);
            
            // 복구 옵션별 평균 페이지수
            Map<ERecoveryOption, Double> avgPageCountMap = recoveryOptionAveragePageCounts.getOrDefault(yearMonth, Map.of());
            Double springAvgPageCount = avgPageCountMap.getOrDefault(ERecoveryOption.SPRING, 0.0);
            Double rawAvgPageCount = avgPageCountMap.getOrDefault(ERecoveryOption.RAW, 0.0);
            Double discardAvgPageCount = avgPageCountMap.getOrDefault(ERecoveryOption.DISCARD, 0.0);
            
            // 복구 옵션별 평균 문서 금액
            Map<ERecoveryOption, Double> avgDocumentPriceMap = recoveryOptionAverageDocumentPrices.getOrDefault(yearMonth,
                    Map.of());
            Double springAvgDocumentPrice = avgDocumentPriceMap.getOrDefault(ERecoveryOption.SPRING, 0.0);
            Double rawAvgDocumentPrice = avgDocumentPriceMap.getOrDefault(ERecoveryOption.RAW, 0.0);
            Double discardAvgDocumentPrice = avgDocumentPriceMap.getOrDefault(ERecoveryOption.DISCARD, 0.0);
            
            // 현재는 하드코딩된 값 (추후 구글 애널리틱스 연동 시 수정)
            Integer pageViewCount = 0;
            Integer visitantCount = 0;
            
            result.add(MonthlyStatisticsDto.of(
                    yearMonth,
                    pageViewCount,
                    visitantCount,
                    signUpCount,
                    orderCount,
                    appliedCount,
                    arrivedCount,
                    completedCount,
                    discardedCount,
                    springCount,
                    rawCount,
                    springAvgPageCount,
                    rawAvgPageCount,
                    discardAvgPageCount,
                    springAvgDocumentPrice,
                    rawAvgDocumentPrice,
                    discardAvgDocumentPrice
            ));
            
            current = current.plusMonths(1);
        }
        
        return result;
    }
}
