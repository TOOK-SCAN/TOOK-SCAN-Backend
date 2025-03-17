package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.mail.event.SendPdfEmailEvent;
import com.tookscan.tookscan.order.application.usecase.SendAdminPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendAdminPdfService implements SendAdminPdfUseCase {

    private final OrderRepository orderRepository;

    private final KakaoMessageUtil kakaoMessageUtil;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final S3Util s3Util;

    @Override
    @Transactional
    public void execute(Long orderId) {

        Order order = orderRepository.findByIdWithDocumentsAndPdfsOrElseThrow(orderId);

        kakaoMessageUtil.sendAnnounceScanFinishMessage(
                order.getUserName(),
                order.getDocumentsDescription(),
                order.getPhoneNumber()
        );

        List<Document> documents = order.getDocuments();

        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        String pdfUrls = documents.stream()
                .map(doc -> doc.getName() + " :<br />" +
                        "<a href=\"" + s3Util.generateSignedUrl(doc) + "\" target=\"_blank\">"
                        + s3Util.generateSignedUrl(doc) + "</a>")
                .reduce((doc1, doc2) -> doc1 + "<br /> <br />" + doc2)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));

        applicationEventPublisher.publishEvent(
                SendPdfEmailEvent.of(
                        order.getDelivery().getEmail(),
                        order.getDocumentsDescription(),
                        pdfUrls
                )
        );

        // 이후 배송할일이 없다면(모든 문서가 폐기라면)
        if (order.getDocuments().stream()
                .noneMatch(document -> document.getRecoveryOption() != ERecoveryOption.DISCARD)) {
            order.updateOrderStatus(EOrderStatus.ALL_COMPLETED);
            orderRepository.save(order);

            // 감사 메세지 전송
            kakaoMessageUtil.sendThanksForUsingMessage(
                    order.getPhoneNumber()
            );
        } else {
            order.updateOrderStatus(EOrderStatus.RECOVERY_IN_PROGRESS);
            orderRepository.save(order);
        }

    }
}
