package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.ExcelUtils;
import com.tookscan.tookscan.message.domain.event.AnnounceDeliveryMessageEvent;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersDeliveriesTrackingNumberUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrdersDeliveriesTrackingNumberService implements
        UpdateAdminOrdersDeliveriesTrackingNumberUseCase {

    private final OrderRepository orderRepository;

    private final DeliveryService deliveryService;
    private final OrderService orderService;

    private final ExcelUtils excelUtils;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void execute(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT, "파일이 비어있습니다.");
        }

        // 1) 엑셀 → (주문번호, 트래킹번호) 리스트
        List<MyOrderExcelRow> rowDataList = excelUtils.parseExcel(file, 1, (row, rowNum) -> {
                    String orderNumber = excelUtils.getStringCellValue(row.getCell(3)); // D열
                    String trackingNumber = excelUtils.getStringCellValue(row.getCell(0)); // A열
                    if (orderNumber.isEmpty() && trackingNumber.isEmpty()) {
                        return null;
                    }
                    // 트래킹 번호에서 하이픈 제거 (1234-5678-9101 → 123456789101)
                    String sanitizedTrackingNumber = sanitizeTrackingNumber(trackingNumber);
                    return new MyOrderExcelRow(orderNumber, sanitizedTrackingNumber);
                })
                .stream()
                .filter(Objects::nonNull)
                .toList();

        // 2) 주문번호만 추출 (중복 제거)
        List<String> orderNumbers = rowDataList.stream()
                .map(MyOrderExcelRow::orderNumber)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .toList();

        // 3) 한 번에 DB 조회
        List<Order> orders = orderRepository.findAllByOrderNumberIn(orderNumbers);

        // 4) 조회된 Order를 맵으로 변환 (orderNumber → Order)
        Map<String, Order> orderMap = orders.stream()
                .collect(Collectors.toMap(Order::getOrderNumber, o -> o));

        List<String> notFoundOrderNumbers = orderNumbers.stream()
                .filter(orderNumber -> !orderMap.containsKey(orderNumber))
                .toList();

        if (!notFoundOrderNumbers.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_ORDER, "주문번호: " + notFoundOrderNumbers);
        }

        // 5) 엑셀 데이터 반복하며 트래킹 번호 업데이트
        for (MyOrderExcelRow rowData : rowDataList) {
            String orderNumber = rowData.orderNumber();
            String trackingNumber = rowData.trackingNumber();

            Order order = orderMap.get(orderNumber);
            
            // Order 상태 검증
            orderService.validateOrderStatus(order, EOrderStatus.POST_WAITING,
                    ErrorCode.INVALID_ORDER_STATUS);
            
            // 트래킹 번호 업데이트
            deliveryService.updateTrackingNumber(order.getDelivery(), trackingNumber);
            
            // 주문 완료 상태로 변경
            orderService.allComplete(order);
            
            // 배송 알림 메시지 이벤트 발행
            applicationEventPublisher.publishEvent(
                    AnnounceDeliveryMessageEvent.of(
                            order.getDocumentsDescription(),
                            order.getDelivery().getTrackingNumber(),
                            order.getDelivery().getId(),
                            order.getDelivery().getPhoneNumber()
                    )
            );
        }
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

    public record MyOrderExcelRow(String orderNumber, String trackingNumber) {
    }
}