package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusCancelUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusCancelRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrdersStatusCancelService implements UpdateAdminOrdersStatusCancelUseCase {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void execute(UpdateAdminOrdersStatusCancelRequestDto requestDto) {
        List<Order> orders = orderRepository.findAllByIdOrElseThrow(requestDto.orderIds());
        orders.forEach(order -> orderService.cancelOrder(order, "관리자 요청에 의한 주문 취소"));
        orderRepository.saveAll(orders);
    }
}
