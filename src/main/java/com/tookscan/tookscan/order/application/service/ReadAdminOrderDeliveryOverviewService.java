package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderDeliveryOverviewResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDeliveryOverviewUseCase;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderDeliveryOverviewService implements ReadAdminOrderDeliveryOverviewUseCase {
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderDeliveryOverviewResponseDto execute(Long orderId) {
        return ReadAdminOrderDeliveryOverviewResponseDto.fromEntity(orderRepository.findByIdOrElseThrow(orderId));
    }
}
