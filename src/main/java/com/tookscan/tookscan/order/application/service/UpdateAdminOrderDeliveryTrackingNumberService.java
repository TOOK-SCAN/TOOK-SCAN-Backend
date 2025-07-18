package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryTrackingNumberUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryTrackingNumberRequestDto;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderDeliveryTrackingNumberService implements UpdateAdminOrderDeliveryTrackingNumberUseCase {
    private final DeliveryRepository deliveryRepository;

    private final DeliveryService deliveryService;
    private final OrderService orderService;

    private final KakaoMessageUtil kakaoMessageUtil;

    @Override
    @Transactional
    public void execute(Long deliveryId, UpdateAdminOrderDeliveryTrackingNumberRequestDto requestDto) {
        Delivery delivery = deliveryRepository.findByIdWithOrderOrElseThrow(deliveryId);
        deliveryService.updateTrackingNumber(
                delivery,
                requestDto.trackingNumber()
        );
        orderService.allComplete(delivery.getOrder());

        deliveryRepository.save(delivery);

        // 운송장 등록 메세지 전송 및 감사 메세지 전송
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kakaoMessageUtil.sendAnnounceDeliveryMessage(
                        delivery.getOrder().getDocumentsDescription(),
                        delivery.getTrackingNumber(),
                        delivery.getOrder().getOrderNumber(),
                        delivery.getPhoneNumber()
                );
                kakaoMessageUtil.sendThanksForUsingMessage(
                        delivery.getPhoneNumber()
                );
            }
        });
    }
}
