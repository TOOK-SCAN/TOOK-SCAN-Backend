package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.mail.domain.event.SendPdfEmailEvent;
import com.tookscan.tookscan.message.domain.event.AnnounceScanFinishMessageEvent;
import com.tookscan.tookscan.order.application.usecase.SendAdminPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendAdminPdfService implements SendAdminPdfUseCase {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final S3Util s3Util;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "send pdf",
        userType = "Admin"
    )
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdWithDocumentsAndDeliveryOrElseThrow(orderId);

        // 주문 상태 검증
        List<EOrderStatus> validStatuses = List.of(
                EOrderStatus.SCAN_COMPLETED,
                EOrderStatus.RECOVERY_IN_PROGRESS,
                EOrderStatus.POST_WAITING,
                EOrderStatus.ALL_COMPLETED
        );

        orderService.validateOrderStatuses(order, validStatuses, ErrorCode.INVALID_ORDER_STATUS);

        // 스캔 완료 알림 메시지 이벤트 발행
        applicationEventPublisher.publishEvent(
                AnnounceScanFinishMessageEvent.of(
                        order.getDelivery().getEmail(),
                        order.getDocumentsDescription(),
                        order.getDelivery().getPhoneNumber()
                )
        );

        List<Document> documents = order.getDocuments();

        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        for (Document document : documents) {
            if (document.getPdfs().isEmpty()) {
                throw new CommonException(ErrorCode.NOT_FOUND_PDF);
            }

            // PDF 파일이 S3에 저장되어 있는지 확인
            for (Pdf pdf : document.getPdfs()) {
                if (pdf.getUploadStatus() != EPdfUploadStatus.COMPLETED) {
                    throw new CommonException(ErrorCode.NOT_PDF_UPLOADED);
                }
                pdf.updatePdfUrlForUser(s3Util.getSignedUrlForUser(pdf));
            }
        }

        String pdfUrls = order.getPdfPresignedUrls();

        applicationEventPublisher.publishEvent(
                SendPdfEmailEvent.of(
                        order.getDelivery().getEmail(),
                        order.getDelivery().getReceiverName(),
                        order.getOrderNumber(),
                        order.getDocumentsDescription(),
                        pdfUrls
                )
        );

        order.updatePdfSendDate(LocalDateTime.now());

        List<EOrderStatus> expectedStatuses = List.of(
                EOrderStatus.RECOVERY_IN_PROGRESS,
                EOrderStatus.POST_WAITING,
                EOrderStatus.ALL_COMPLETED
        );

        if (expectedStatuses.contains(order.getOrderStatus())) {
            orderRepository.save(order);
            LogContext.put("order_id", orderId);
            return;
        }

        // 이후 배송할일이 없다면(모든 문서가 폐기라면)
        if (order.getDocuments().stream()
                .noneMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD)) {
            orderService.allComplete(order);
        } else {
            orderService.startRecovery(order);
        }
        orderRepository.save(order);
        LogContext.put("order_id", orderId);
    }
}
