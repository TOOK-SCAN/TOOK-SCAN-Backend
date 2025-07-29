package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.presentation.dto.response.ReadStatisticsSummariesResponseDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadStatisticsSummariesResponseDto.MonthlyStatisticsDto;
import com.tookscan.tookscan.order.application.usecase.ReadStatisticsSummariesUseCase;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        LocalDate start = LocalDate.parse(startYearMonth + "-01");
        LocalDate end = LocalDate.parse(endYearMonth + "-01");

        // 1. 트랜잭션 내에서 모든 DB 조회 결과만 수집
        List<MonthlyStatisticsDto> statistics = loadStatistics(start, end);

        // 2. 트랜잭션 밖에서 정렬 처리
        statistics.sort((a, b) -> b.getYearMonth().compareTo(a.getYearMonth()));

        // 3. 최종 응답 객체 구성
        return ReadStatisticsSummariesResponseDto.from(statistics);
    }

    @Transactional(readOnly = true)
    protected List<MonthlyStatisticsDto> loadStatistics(LocalDate start, LocalDate end) {
        List<MonthlyStatisticsDto> result = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end)) {
            LocalDateTime monthStart = current.atStartOfDay();
            LocalDateTime monthEnd = current.plusMonths(1).atStartOfDay();

            Integer signUpCount = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            Integer orderCount = orderRepository.countByCreatedAtBetween(monthStart, monthEnd);
            Integer totalAmount = paymentRepository.sumTotalAmountByCreatedAtBetween(monthStart, monthEnd);
            if (totalAmount == null) totalAmount = 0;

            String yearMonth = String.format("%04d-%02d", current.getYear(), current.getMonthValue());

            Integer pageViewCount = 0;
            Integer visitantCount = 0;

            Integer appliedCount = orderRepository.countByCreatedAtBetweenAndOrderStatus(monthStart, monthEnd, EOrderStatus.APPLY_COMPLETED);
            Integer arrivedCount = orderRepository.countByCreatedAtBetweenAndOrderStatus(monthStart, monthEnd, EOrderStatus.COMPANY_ARRIVED);
            Integer completedCount = orderRepository.countByCreatedAtBetweenAndOrderStatus(monthStart, monthEnd, EOrderStatus.ALL_COMPLETED);

            Integer discardedCount = orderRepository.countByCreatedAtBetweenAndRecoveryOption(monthStart, monthEnd, ERecoveryOption.DISCARD);
            Integer springCount = orderRepository.countByCreatedAtBetweenAndRecoveryOption(monthStart, monthEnd, ERecoveryOption.SPRING);
            Integer rawCount = orderRepository.countByCreatedAtBetweenAndRecoveryOption(monthStart, monthEnd, ERecoveryOption.RAW);

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
                    rawCount
            ));

            current = current.minusMonths(1);
        }

        return result;
    }
}
