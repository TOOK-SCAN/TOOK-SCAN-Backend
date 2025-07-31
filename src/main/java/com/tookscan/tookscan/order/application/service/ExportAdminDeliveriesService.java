package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.ExcelUtils;
import com.tookscan.tookscan.order.application.usecase.ExportAdminDeliveriesUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.request.ExportAdminDeliveriesRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExportAdminDeliveriesService implements ExportAdminDeliveriesUseCase {

    private final OrderRepository orderRepository;
    private final ExcelUtils excelUtils;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "export deliveries",
        userType = "Admin"
    )
    public byte[] execute(ExportAdminDeliveriesRequestDto requestDto) {
        List<Order> orders = orderRepository.findAllByIdOrElseThrow(requestDto.orderIds());

        orders.forEach(order -> {
            if (order.getOrderStatus() != EOrderStatus.POST_WAITING) {
                throw new CommonException(ErrorCode.NOT_POST_WAITING_ORDER,
                        "주문 ID: " + order.getId());
            }
        });

        LogContext.put("exported_orders_count", orders.size());
        
        return excelUtils.writeDeliveries(orders);
    }
}
