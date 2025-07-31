package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusRecoveryOptionUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusRecoveryOptionRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrdersStatusRecoveryOptionService implements UpdateAdminOrdersStatusRecoveryOptionUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "update orders status to recovery completed",
        userType = "Admin"
    )
    public void execute(UUID accountId, UpdateAdminOrdersStatusRecoveryOptionRequestDto requestDto) {
        List<Order> orders = orderRepository.findAllByIdOrElseThrow(requestDto.orderIds());
        
        orders.forEach(orderService::completeRecovery);
        
        orderRepository.saveAll(orders);
        
        LogContext.put("updated_orders_count", orders.size());
    }
}
