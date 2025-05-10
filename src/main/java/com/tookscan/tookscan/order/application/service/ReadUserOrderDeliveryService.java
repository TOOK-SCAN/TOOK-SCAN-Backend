package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.DeliveryTrackerUtil;
import com.tookscan.tookscan.order.presentation.dto.response.ReadUserOrderDeliveryResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadUserOrderDeliveryUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadUserOrderDeliveryService implements ReadUserOrderDeliveryUseCase {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final DeliveryTrackerUtil deliveryTrackerUtil;

    @Override
    @Transactional
    public ReadUserOrderDeliveryResponseDto execute(UUID accountId, Long orderId) {
        User user = userRepository.findByIdOrElseThrow(accountId);
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.validateOrderUser(order, user);

        if (order.getOrderStatus() != EOrderStatus.ALL_COMPLETED) {
            throw new CommonException(ErrorCode.INVALID_ORDER_STATUS, "작업이 완료되지 않은 주문입니다.");
        }

        List<Map<String, Object>> trackingResponse = deliveryTrackerUtil.trackDelivery(
                order.getDelivery().getTrackingNumber());
        String carrierName = deliveryTrackerUtil.getCarrierName();

        return ReadUserOrderDeliveryResponseDto.of(order, trackingResponse, carrierName);
    }
}