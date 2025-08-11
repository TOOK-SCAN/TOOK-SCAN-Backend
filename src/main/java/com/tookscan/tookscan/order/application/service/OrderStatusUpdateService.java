package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.application.usecase.UpdateOrderStatusAfterPdfProcessingUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusUpdateService implements UpdateOrderStatusAfterPdfProcessingUseCase {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Long orderId) {
        try {
            Order order = orderRepository.findByIdOrElseThrow(orderId);

            List<EOrderStatus> validStatuses = List.of(
                    EOrderStatus.RECOVERY_IN_PROGRESS,
                    EOrderStatus.POST_WAITING,
                    EOrderStatus.ALL_COMPLETED
            );

            if (validStatuses.contains(order.getOrderStatus())) {
                order.updateScanCompletedAt(LocalDateTime.now());
                return;
            }

            boolean hasAnyPdf = order.getDocuments().stream().anyMatch(doc -> !doc.getPdfs().isEmpty());
            boolean allDocumentsHavePdf = order.getDocuments().stream().noneMatch(doc -> doc.getPdfs().isEmpty());

            if (allDocumentsHavePdf) {
                orderService.completeScan(order);
            } else if (hasAnyPdf) {
                orderService.startScan(order);
            }
        } catch (Exception e) {
            log.error("Failed to update order status for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }
}

