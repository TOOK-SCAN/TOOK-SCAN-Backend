package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrdersStatusService implements UpdateAdminOrdersStatusUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "update orders status",
        userType = "Admin"
    )
    public void execute(UpdateAdminOrdersStatusRequestDto requestDto) {

        if (requestDto.status().equals(EOrderStatus.PAYMENT_WAITING) ||
                requestDto.status().equals(EOrderStatus.PAYMENT_COMPLETED)
        ) {
            throw new CommonException(ErrorCode.INVALID_ENUM_TYPE);
        }

        List<Order> orders = orderRepository.findAllByIdOrElseThrow(requestDto.orderIds());

        orders.forEach(
                order -> {
                    orderService.updateOrderStatus(order, requestDto.status());
                }
        );

        orderRepository.saveAll(orders);
        
        LogContext.put("updated_orders_count", orders.size());
        LogContext.put("new_status", requestDto.status().name());
    }

}
