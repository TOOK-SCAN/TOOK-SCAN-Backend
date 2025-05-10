package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderSummariesResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderSummariesUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderSummariesService implements ReadAdminOrderSummariesUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderSummariesResponseDto execute(Integer page, Integer size, String startDate, String endDate,
                                                      String search, String searchType, String sort,
                                                      Direction direction) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> orderIdPages = orderRepository.findOrderSummaries(startDate, endDate, search, searchType,
                sort, direction, pageable);

        List<Long> orderIds = orderIdPages.getContent();

        List<Order> orders = orderRepository.findAllWithDocumentsByIdIn(orderIdPages.getContent());

        Map<Long, Order> orderMap = orders.stream()
                .collect(Collectors.toMap(Order::getId, Function.identity()));
        List<Order> sortedOrders = orderIds.stream()
                .map(orderMap::get)
                .toList();

        PageInfoDto pageInfo = PageInfoDto.fromEntity(orderIdPages);

        return ReadAdminOrderSummariesResponseDto.of(sortedOrders, pageInfo);
    }
}
