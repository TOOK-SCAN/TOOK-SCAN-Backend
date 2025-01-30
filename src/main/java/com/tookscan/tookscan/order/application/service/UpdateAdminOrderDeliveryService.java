package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.dto.request.UpdateAdminOrderDeliveryRequestDto;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.repository.mysql.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAdminOrderDeliveryService implements UpdateAdminOrderDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    @Override
    public void execute(Long deliveryId, UpdateAdminOrderDeliveryRequestDto requestDto) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DELIVERY, "배송 ID: " + deliveryId));

        deliveryService.updateSelf(
                delivery,
                requestDto.receiverName(),
                requestDto.phoneNumber(),
                requestDto.address().toEntity(),
                requestDto.request(),
                requestDto.trackingNumber(),
                requestDto.email()
        );
    }
}
