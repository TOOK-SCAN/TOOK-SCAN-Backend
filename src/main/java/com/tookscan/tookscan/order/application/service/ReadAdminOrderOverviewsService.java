package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderOverviewsUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderOverviewsResponseDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderOverviewsService implements ReadAdminOrderOverviewsUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderOverviewsResponseDto execute(int page, int size, String startDate, String endDate,
                                                      String search, String searchType, String sort,
                                                      Direction direction, EOrderStatus orderStatus,
                                                      Boolean isOneDayScan, Boolean hasRecoveryOption,
                                                      Boolean isAsInProgress, Boolean isInProgress, UUID customerKey) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 필터링된 주문 조회 (페이지네이션)
        Page<Long> orderIdPages = orderRepository.findOrderOverviews(startDate, endDate, search,
                searchType, sort, direction, pageable, orderStatus, isOneDayScan, hasRecoveryOption, isAsInProgress,
                isInProgress, customerKey);

        List<Order> filteredOrders = orderRepository.findAllWithDocumentsByIdIn(orderIdPages.getContent());

        // 상태별 개수 조회 (isInProgress 필터만 적용)
        Map<EOrderStatus, Long> statusCounts = orderRepository.findOrderStatusCountsByIsInProgress(isInProgress);
        
        // 전체 통계 조회 (모든 필터 제외)
        Map<EOrderStatus, Long> overallStatusCounts = orderRepository.findOrderStatusCountsByIsInProgress(null);

        return ReadAdminOrderOverviewsResponseDto.of(filteredOrders, statusCounts, overallStatusCounts, orderIdPages);
    }
}
