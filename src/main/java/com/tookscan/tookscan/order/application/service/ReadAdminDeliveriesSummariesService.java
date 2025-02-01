package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadAdminDeliveriesSummariesResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminDeliveriesSummariesUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminDeliveriesSummariesService implements ReadAdminDeliveriesSummariesUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminDeliveriesSummariesResponseDto execute(Integer page, Integer size, String startDate, String endDate,
                                                           String search, String searchType) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> orderIdPages = orderRepository.findDeliveriesSummaries(startDate, endDate, search, searchType,
                pageable);

        List<Order> orders = orderRepository.findAllWithDocumentsAndUserByIdIn(orderIdPages.getContent());

        return ReadAdminDeliveriesSummariesResponseDto.of(orders, orderIdPages);
    }
}
