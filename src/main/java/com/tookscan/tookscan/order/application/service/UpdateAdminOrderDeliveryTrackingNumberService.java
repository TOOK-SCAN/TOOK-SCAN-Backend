package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.order.application.dto.request.UpdateAdminOrderDeliveryTrackingNumberRequestDto;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryTrackingNumberUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderDeliveryTrackingNumberService implements UpdateAdminOrderDeliveryTrackingNumberUseCase {
    private final DeliveryRepository deliveryRepository;

    private final KakaoMessageUtil kakaoMessageUtil;

    @Value("${solapi.sender}")
    private String sender;

    @Override
    @Transactional
    public void execute(Long deliveryId, UpdateAdminOrderDeliveryTrackingNumberRequestDto requestDto) {
        Delivery delivery = deliveryRepository.findByIdWithOrderOrElseThrow(deliveryId);
        delivery.updateTrackingNumber(requestDto.trackingNumber());
        delivery.getOrder().updateOrderStatus(EOrderStatus.ALL_COMPLETED);

        deliveryRepository.save(delivery);

        // 운송장 등록 메세지 전송
        kakaoMessageUtil.sendAnnounceDeliveryMessage(
                delivery.getOrder().getUser() != null ? delivery.getOrder().getUser().getPhoneNumber() : delivery.getPhoneNumber(),
                sender
        );


        // 감사 메세지 전송
        kakaoMessageUtil.sendThanksForUsingMessage(
                delivery.getOrder().getUser() != null ? delivery.getOrder().getUser().getPhoneNumber() : delivery.getPhoneNumber(),
                sender
        );
    }
}
