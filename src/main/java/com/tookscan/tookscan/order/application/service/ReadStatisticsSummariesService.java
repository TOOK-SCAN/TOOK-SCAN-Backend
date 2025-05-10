package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.repository.UserRepository;
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
    @Transactional
    public ReadStatisticsSummariesResponseDto execute(String startYearMonth, String endYearMonth) {
        LocalDate start = LocalDate.parse(startYearMonth + "-01");
        LocalDate end = LocalDate.parse(endYearMonth + "-01");
        // 결과를 담을 리스트
        List<MonthlyStatisticsDto> result = new ArrayList<>();

        // 2) start부터 end까지 월단위로 반복
        LocalDate current = start;
        while (!current.isAfter(end)) {
            // 예: current = 2023-01-01 → 해당 월의 시작 시각
            LocalDateTime monthStart = current.atStartOfDay();
            // 다음 달 1일 자정
            LocalDateTime monthEnd = current.plusMonths(1).atStartOfDay();

            // 3) 각 달별로 회원가입 수, 주문 수, 총 결제액 조회
            Integer signUpCount = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            Integer orderCount = orderRepository.countByCreatedAtBetween(monthStart, monthEnd);
            Integer totalAmount = paymentRepository.sumTotalAmountByCreatedAtBetween(monthStart, monthEnd);

            // NPE 방지 (DB가 null 반환할 수 있으므로)
            if (totalAmount == null) {
                totalAmount = 0;
            }

            // 4) yearMonth 형식 문자열 구성
            String yearMonth = String.format("%04d-%02d", current.getYear(), current.getMonthValue());

            // TODO: page view count, visitant count 추가
            Integer pageViewCount = 0;
            Integer visitantCount = 0;

            // 5) DTO에 담아서 결과 리스트에 추가
            result.add(MonthlyStatisticsDto.of(yearMonth, pageViewCount, visitantCount, signUpCount, orderCount,
                    totalAmount));

            // 다음 달로 이동
            current = current.plusMonths(1);
        }

        // 모든 달에 대한 통계를 구한 뒤 반환
        return ReadStatisticsSummariesResponseDto.from(result);
    }
}
