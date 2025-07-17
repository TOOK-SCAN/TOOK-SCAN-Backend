package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.DeleteOldOrdersUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteOldOrdersService implements DeleteOldOrdersUseCase {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void execute() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Order> orders = orderRepository.findAllByCreatedAtBeforeWithEOrderStatus(twoWeeksAgo,
                EOrderStatus.APPLY_COMPLETED);
        orders.forEach(order -> orderService.cancelOrder(order, "2주 이상 지난 주문 자동 삭제"));
        orderRepository.saveAll(orders);
    }
}
