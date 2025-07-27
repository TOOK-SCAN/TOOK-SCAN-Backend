package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.address.domain.service.AddressService;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderInfoUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderInfoRequestDto;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserOrderInfoService implements UpdateUserOrderInfoUseCase {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final OrderService orderService;
    private final AddressService addressService;

    @Override
    @Transactional
    public void execute(UUID accountId, Long orderId, UpdateUserOrderInfoRequestDto requestDto) {
        User user = userRepository.findByIdOrElseThrow(accountId);
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.validateOrderUser(order, user);
        if (requestDto.address() != null || requestDto.deliveryRequest() != null) {
            orderService.validateUpdatableOrder(order);
        }
        
        orderService.validateUpdatableOrderInfo(order);

        order.getDelivery().updateEmail(requestDto.email());
        Address address = addressService.createAddress(
                requestDto.address().addressName(),
                requestDto.address().region1DepthName(),
                requestDto.address().region2DepthName(),
                requestDto.address().region3DepthName(),
                requestDto.address().region4DepthName(),
                requestDto.address().addressDetail(),
                requestDto.address().zoneCode(),
                requestDto.address().latitude(),
                requestDto.address().longitude()
        );
        order.getDelivery().updateAddress(address);
        order.getDelivery().updateRequest(requestDto.deliveryRequest());
        orderRepository.save(order);
    }
}
