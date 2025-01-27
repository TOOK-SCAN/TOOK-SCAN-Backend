package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.dto.PageInfoDto;
import com.tookscan.tookscan.order.application.dto.response.ReadAdminOrderSummariesResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderSummariesUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.mysql.OrderRepository;
import com.tookscan.tookscan.order.repository.mysql.custom.OrderRepositoryCustomImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderSummariesService implements ReadAdminOrderSummariesUseCase {

    private final OrderRepository orderRepository;
    private final OrderRepositoryCustomImpl orderRepositoryCustomImpl;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderSummariesResponseDto execute(Integer page, Integer size, String startDate, String endDate,
                                                      String search, String searchType, String sort, String direction) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Long> orderIdPages = orderRepositoryCustomImpl.findOrderSummaries(page, size, startDate, endDate, search, searchType, sort, direction, pageable);

        List<Order> orders = orderRepository.findAllWithDocumentsByIdIn(orderIdPages.getContent());

        PageInfoDto pageInfo = PageInfoDto.fromEntity(orderIdPages);

        return ReadAdminOrderSummariesResponseDto.of(orders, pageInfo);
    }
}
