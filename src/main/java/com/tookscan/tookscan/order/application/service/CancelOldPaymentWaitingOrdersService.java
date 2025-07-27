package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.CancelOldPaymentWaitingOrdersUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelOldPaymentWaitingOrdersService implements CancelOldPaymentWaitingOrdersUseCase {

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    @Override
    @Async("schedulerTaskExecutor")
    @Transactional
    public void execute() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        List<Order> orders = orderRepository.findAllByCreatedAtBeforeWithEOrderStatus(twoWeeksAgo,
                EOrderStatus.PAYMENT_WAITING);
        
        log.info("Found {} payment waiting orders to cancel (created before {})", orders.size(), twoWeeksAgo);
        
        orders.forEach(order -> orderService.cancelOrder(order, "2주 이상 지난 결제대기 주문 자동 취소"));
        orderRepository.saveAll(orders);
        
        log.info("Successfully cancelled {} payment waiting orders", orders.size());
    }
}