package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.message.domain.event.AnnounceDeliveryMessageEvent;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderDeliveryTrackingNumberUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderDeliveryTrackingNumberRequestDto;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import java.util.List;
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
    @BusinessLog(
        domain = "Order",
        action = "update tracking number",
        userType = "Admin"
    )
    public void execute(Long deliveryId, UpdateAdminOrderDeliveryTrackingNumberRequestDto requestDto) {
        Delivery delivery = deliveryRepository.findByIdWithOrderOrElseThrow(deliveryId);

        List<EOrderStatus> validStatuses = List.of(
                EOrderStatus.POST_WAITING,
                EOrderStatus.ALL_COMPLETED
        );

        orderService.validateOrderStatuses(delivery.getOrder(), validStatuses,
                ErrorCode.INVALID_ORDER_STATUS);

        // 트래킹 번호 하이픈 제거 후 업데이트
        String sanitizedTrackingNumber = sanitizeTrackingNumber(requestDto.trackingNumber());
        deliveryService.updateTrackingNumber(
                delivery,
                sanitizedTrackingNumber
        );
        orderService.allComplete(delivery.getOrder());

        deliveryRepository.save(delivery);

        applicationEventPublisher.publishEvent(
                AnnounceDeliveryMessageEvent.of(
                        delivery.getOrder().getDocumentsDescription(),
                        delivery.getTrackingNumber(),
                        delivery.getId(),
                        delivery.getPhoneNumber()
                )
        );
        
        LogContext.put("delivery_id", deliveryId);
        LogContext.put("order_id", delivery.getOrder().getId());
    }

    /**
     * 트래킹 번호에서 하이픈을 제거하여 DB 저장용으로 변환 예: "1234-5678-9101" → "123456789101"
     *
     * @param trackingNumber 원본 트래킹 번호
     * @return 하이픈이 제거된 트래킹 번호
     */
    public static String sanitizeTrackingNumber(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            return trackingNumber;
        }

        // 하이픈 제거 및 공백 제거
        String sanitized = trackingNumber.replaceAll("-", "").trim();

        // 12자리 숫자 형식 검증
        if (!sanitized.matches("\\d{12}")) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT,
                    "트래킹 번호는 12자리 숫자여야 합니다. 입력값: " + trackingNumber);
        }

        return sanitized;
    }

}
