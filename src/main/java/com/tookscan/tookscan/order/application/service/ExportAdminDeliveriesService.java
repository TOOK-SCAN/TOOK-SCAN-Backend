package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.core.utility.ExcelUtils;
import com.tookscan.tookscan.order.presentation.dto.request.ExportAdminDeliveriesRequestDto;
import com.tookscan.tookscan.order.application.usecase.ExportAdminDeliveriesUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExportAdminDeliveriesService implements ExportAdminDeliveriesUseCase {

    private final OrderRepository orderRepository;
    private final ExcelUtils excelUtils;

    @Override
    @Transactional
    public byte[] execute(ExportAdminDeliveriesRequestDto requestDto) {
        LocalDateTime startDate = DateTimeUtil.convertStringToDartDate(requestDto.startDate()).atStartOfDay();
        LocalDateTime endDate = DateTimeUtil.convertStringToDartDate(requestDto.endDate()).plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findAllByOrderStatusDateBetweenOrElseThrow(startDate, endDate,
                EOrderStatus.POST_WAITING);

        return excelUtils.writeDeliveries(orders);
    }
}
