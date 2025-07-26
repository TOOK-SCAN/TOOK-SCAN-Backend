package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersStatusCompanyArrivedUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrdersStatusCompanyArrivedRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrdersStatusCompanyArrivedService implements UpdateAdminOrdersStatusCompanyArrivedUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public void execute(UpdateAdminOrdersStatusCompanyArrivedRequestDto requestDto) {
        List<Order> orders = orderRepository.findAllByIdOrElseThrow(requestDto.orderIds());

        orders.forEach(orderService::arriveCompany);
        orderRepository.saveAll(orders);
    }
}