package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDetailUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderDetailResponseDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderDetailService implements ReadAdminOrderDetailUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderDetailResponseDto execute(Long orderId) {
        Order order = orderRepository.findByIdWithDocumentsAndPdfsAndDeliveryOrElseThrow(orderId);

        return ReadAdminOrderDetailResponseDto.fromEntity(order);
    }
}
