package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.CancelOldApplyCompletedOrdersUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOldApplyCompletedOrdersService implements CancelOldApplyCompletedOrdersUseCase {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    @Override
    @Async("schedulerTaskExecutor")
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "cancel old apply completed orders",
        userType = "System"
    )
    public void execute() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Order> orders = orderRepository.findAllByCreatedAtBeforeWithEOrderStatus(twoWeeksAgo,
                EOrderStatus.APPLY_COMPLETED);

        orders.forEach(order -> orderService.cancelOrder(order, "2주 이상 지난 신청완료 주문 자동 취소"));
        orderRepository.saveAll(orders);

        LogContext.put("cancelled_orders_count", orders.size());
    }
}
