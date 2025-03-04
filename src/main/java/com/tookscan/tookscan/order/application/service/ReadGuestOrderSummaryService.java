package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderSummaryResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderSummaryUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderSummaryService implements ReadGuestOrderSummaryUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadGuestOrderSummaryResponseDto execute(String orderNumber) {
        Order order = orderRepository.findByOrderNumberOrElseThrow(orderNumber);

        return ReadGuestOrderSummaryResponseDto.fromEntity(order);
    }
}
