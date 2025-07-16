package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderStatusCompanyArrivedUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderStatusCompanyArrivedService implements UpdateAdminOrderStatusCompanyArrivedUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public void execute(Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        
        orderService.arriveCompany(order);
        orderRepository.save(order);
    }
}