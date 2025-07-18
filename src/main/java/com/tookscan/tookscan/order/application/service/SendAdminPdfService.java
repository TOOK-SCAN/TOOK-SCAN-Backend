package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.mail.event.SendPdfEmailEvent;
import com.tookscan.tookscan.order.application.usecase.SendAdminPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.service.PdfService;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class SendAdminPdfService implements SendAdminPdfUseCase {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final KakaoMessageUtil kakaoMessageUtil;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PdfService pdfService;

    @Override
    @Transactional
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdWithDocumentsAndDeliveryOrElseThrow(orderId);

        kakaoMessageUtil.sendAnnounceScanFinishMessage(
                order.getDelivery().getEmail(),
                order.getDocumentsDescription(),
                order.getDelivery().getPhoneNumber()
        );

        List<Document> documents = order.getDocuments();

        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        String pdfUrls = pdfService.getPdfUrls(order.getDocuments());

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

        // 이후 배송할일이 없다면(모든 문서가 폐기라면)
        if (order.getDocuments().stream()
                .noneMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD)) {
            orderService.allComplete(order);
            orderRepository.save(order);

            // 감사 메세지 전송
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    kakaoMessageUtil.sendThanksForUsingMessage(
                            order.getDelivery().getPhoneNumber()
                    );
                }
            });
        } else {
            orderService.startRecovery(order);
            orderRepository.save(order);
        }

    }
}
