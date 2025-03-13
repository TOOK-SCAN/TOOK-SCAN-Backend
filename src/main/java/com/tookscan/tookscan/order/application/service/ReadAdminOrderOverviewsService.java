package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderOverviewsResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderOverviewsUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadAdminOrderOverviewsService implements ReadAdminOrderOverviewsUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderOverviewsResponseDto execute(int page, int size, String startDate, String endDate,
                                                      String search, String searchType, String sort,
                                                      Direction direction, EOrderStatus orderStatus) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> orderIdPages = orderRepository.findOrderOverviews(startDate, endDate, search,
                searchType, sort, direction, pageable, orderStatus);

        List<Order> orders = orderRepository.findAllWithDocumentsByIdIn(orderIdPages.getContent());

        return ReadAdminOrderOverviewsResponseDto.of(orders, orderIdPages);
    }
}
