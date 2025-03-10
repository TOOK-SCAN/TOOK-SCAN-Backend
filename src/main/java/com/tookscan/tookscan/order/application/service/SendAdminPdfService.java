package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.mail.event.SendPdfEmailEvent;
import com.tookscan.tookscan.order.application.usecase.SendAdminPdfUseCase;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendAdminPdfService implements SendAdminPdfUseCase {

    private final OrderRepository orderRepository;

    private final KakaoMessageUtil kakaoMessageUtil;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${solapi.sender}")
    private String sender;

    @Override
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdWithDocumentsAndPdfsOrElseThrow(orderId);

        kakaoMessageUtil.sendAnnounceScanFinishMessage(
                order.getUserName(),
                order.getDocumentsDescription(),
                order.getPhoneNumber(),
                sender
        );

        applicationEventPublisher.publishEvent(
                SendPdfEmailEvent.of(
                        order.getDelivery().getEmail(),
                        order.getDocumentsDescription(),
                        order.getPdfUrls()
                )
        );

        // 이후 배송할일이 없다면(모든 문서가 폐기라면)
        if (order.getDocuments().stream()
                .noneMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD)) {
            order.updateOrderStatus(EOrderStatus.ALL_COMPLETED);
            orderRepository.save(order);

            // 감사 메세지 전송
            kakaoMessageUtil.sendThanksForUsingMessage(
                    order.getPhoneNumber(),
                    sender
            );
        }
    }
}
