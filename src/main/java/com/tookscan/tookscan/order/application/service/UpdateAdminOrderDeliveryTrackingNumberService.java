package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.message.event.AnnounceDeliveryMessageEvent;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryTrackingNumberUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryTrackingNumberRequestDto;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderDeliveryTrackingNumberService implements UpdateAdminOrderDeliveryTrackingNumberUseCase {
    private final DeliveryRepository deliveryRepository;

    private final DeliveryService deliveryService;
    private final OrderService orderService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void execute(Long deliveryId, UpdateAdminOrderDeliveryTrackingNumberRequestDto requestDto) {
        Delivery delivery = deliveryRepository.findByIdWithOrderOrElseThrow(deliveryId);

        orderService.validateOrderStatus(delivery.getOrder(), EOrderStatus.POST_WAITING,
                ErrorCode.INVALID_ORDER_STATUS);

        deliveryService.updateTrackingNumber(
                delivery,
                requestDto.trackingNumber()
        );
        orderService.allComplete(delivery.getOrder());

        deliveryRepository.save(delivery);

        applicationEventPublisher.publishEvent(
                AnnounceDeliveryMessageEvent.of(
                        delivery.getOrder().getDocumentsDescription(),
                        delivery.getTrackingNumber(),
                        delivery.getOrder().getOrderNumber(),
                        delivery.getPhoneNumber()
                )
        );
    }
}
