package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderOverviewsUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderOverviewsResponseDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
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
                                                      Boolean isAsInProgress, Boolean isInProgress) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> orderIdPages = orderRepository.findOrderOverviews(startDate, endDate, search,
                searchType, sort, direction, pageable, orderStatus, isOneDayScan, hasRecoveryOption, isAsInProgress,
                isInProgress);

        List<Order> orders = orderRepository.findAllWithDocumentsByIdIn(orderIdPages.getContent());

        return ReadAdminOrderOverviewsResponseDto.of(orders, orderIdPages);
    }
}
