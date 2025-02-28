package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.DeliveryTrackerUtil;
import com.tookscan.tookscan.order.application.dto.response.ReadGuestOrderDeliveryResponseDto;
import com.tookscan.tookscan.order.application.usecase.ReadGuestOrderDeliveryUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadGuestOrderDeliveryService implements ReadGuestOrderDeliveryUseCase {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final DeliveryTrackerUtil deliveryTrackerUtil;

    @Override
    @Transactional
    public ReadGuestOrderDeliveryResponseDto execute(Long orderId) {
        Order order = orderRepository.findByIdOrElseThrow(orderId);

        if (order.getOrderStatus() != EOrderStatus.ALL_COMPLETED) {
            throw new CommonException(ErrorCode.INVALID_ORDER_STATUS, "작업이 완료되지 않은 주문입니다.");
        }

        List<Map<String, Object>> trackingResponse = deliveryTrackerUtil.trackDelivery(
                order.getDelivery().getTrackingNumber());
        String carrierName = deliveryTrackerUtil.getCarrierName();

        return ReadGuestOrderDeliveryResponseDto.of(order, trackingResponse, carrierName);
    }
}