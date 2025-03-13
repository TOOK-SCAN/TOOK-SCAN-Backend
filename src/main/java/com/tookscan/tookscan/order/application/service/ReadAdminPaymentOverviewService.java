package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.dto.response.ReadAdminPaymentOverviewResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminPaymentOverviewUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminPaymentOverviewService implements ReadAdminPaymentOverviewUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminPaymentOverviewResponseDto execute(Long orderId) {
        // 주문 조회
        Order order = orderRepository.findByIdWithDocumentsOrElseThrow(orderId);

        return ReadAdminPaymentOverviewResponseDto.of(order);
    }
}
