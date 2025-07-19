package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.ExcelUtils;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrdersDeliveriesTrackingNumberUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrdersDeliveriesTrackingNumberService implements
        UpdateAdminOrdersDeliveriesTrackingNumberUseCase {

    private final OrderRepository orderRepository;

    private final DeliveryService deliveryService;

    private final ExcelUtils excelUtils;

    @Override
    @Transactional
    public void execute(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT, "파일이 비어있습니다.");
        }

        // 1) 엑셀 → (주문번호, 트래킹번호) 리스트
        List<MyOrderExcelRow> rowDataList = excelUtils.parseExcel(file, 1, (row, rowNum) -> {
                    String orderNumber = excelUtils.getStringCellValue(row.getCell(0)); // A열
                    String trackingNumber = excelUtils.getStringCellValue(row.getCell(5)); // F열
                    if (orderNumber.isEmpty() && trackingNumber.isEmpty()) {
                        return null;
                    }
                    return new MyOrderExcelRow(orderNumber, trackingNumber);
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
            deliveryService.updateTrackingNumber(order.getDelivery(), trackingNumber);
        }
    }

    public record MyOrderExcelRow(String orderNumber, String trackingNumber) {
    }
}