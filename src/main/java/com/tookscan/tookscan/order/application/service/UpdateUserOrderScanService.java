package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderScanUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.mysql.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserOrderScanService implements UpdateUserOrderScanUseCase {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(UUID accountId, Long orderId) {
        // 사용자 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ORDER));

        // 주문자 확인
        orderService.validateOrderUser(order, user);

        // 결제 상태 확인
        orderService.validateOrderStatus(order, EOrderStatus.PAYMENT_COMPLETED, ErrorCode.PAYMENT_INCOMPLETE);

        // 주문 상태 변경
        orderService.updateOrderStatus(order, EOrderStatus.SCAN_WAITING);
    }
}
