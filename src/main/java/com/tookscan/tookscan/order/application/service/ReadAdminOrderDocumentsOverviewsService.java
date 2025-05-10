package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.ScannerUtil;
import com.tookscan.tookscan.order.presentation.dto.response.ReadAdminOrderDocumentsOverviewsResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadAdminOrderDocumentsOverviewsUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.EScanStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadAdminOrderDocumentsOverviewsService implements ReadAdminOrderDocumentsOverviewsUseCase {

    private final OrderRepository orderRepository;

    private final ScannerUtil scannerUtil;

    @Override
    @Transactional(readOnly = true)
    public ReadAdminOrderDocumentsOverviewsResponseDto execute(Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        Map<Long, EScanStatus> scanStatuses = scannerUtil.getScanStatuses(order.getDocuments());

        if (order.getOrderStatus() == EOrderStatus.APPLY_COMPLETED
                || order.getOrderStatus() == EOrderStatus.COMPANY_ARRIVED
                || order.getOrderStatus() == EOrderStatus.PAYMENT_WAITING
                || order.getOrderStatus() == EOrderStatus.PAYMENT_COMPLETED) {
            scanStatuses.replaceAll((k, v) -> EScanStatus.UNABLE);
        }

        return ReadAdminOrderDocumentsOverviewsResponseDto.of(order, scanStatuses);
    }
}
