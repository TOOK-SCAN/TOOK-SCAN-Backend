package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.CreateAdminOrderMemoUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.presentation.dto.request.CreateAdminOrderMemoRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminOrderMemoService implements CreateAdminOrderMemoUseCase {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "create order memo",
        userType = "Admin"
    )
    public void execute(Long orderId, CreateAdminOrderMemoRequestDto requestDto) {
        // 주문 메모 생성
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        order.createMemo(requestDto.content());
        order.updateAsInProgress(requestDto.isAsInProgress());
        orderRepository.save(order);
        
        LogContext.put("order_id", order.getId());
    }
}
