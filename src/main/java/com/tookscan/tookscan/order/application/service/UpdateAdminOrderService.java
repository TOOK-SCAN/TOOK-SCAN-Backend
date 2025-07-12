package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.application.usecase.UpdateAdminOrderUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.DocumentService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateAdminOrderRequestDto;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAdminOrderService implements UpdateAdminOrderUseCase {

    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Override
    @Transactional
    public void execute(Long orderId, UpdateAdminOrderRequestDto requestDto) {
        // 주문(Order) 엔티티 조회
        Order order = orderRepository.findByIdWithDocumentsAndPdfsAndDeliveryOrElseThrow(orderId);
        Set<Long> orderDocumentIds = order.getDocuments().stream()
                .map(Document::getId)
                .collect(Collectors.toSet());

        // 요청으로 들어온 문서들을 신규 문서(id == null)와 기존 문서(id != null)로 분리
        List<UpdateAdminOrderRequestDto.DocumentDto> newDocuments = requestDto.documents().stream()
                .filter(doc -> doc.id() == null)
                .toList();

        List<UpdateAdminOrderRequestDto.DocumentDto> existingDocuments = requestDto.documents().stream()
                .filter(doc -> doc.id() != null)
                .toList();

        // 기존 문서의 경우, 해당 주문에 속한 문서인지 검증
        List<Long> existingDocumentIds = existingDocuments.stream()
                .map(UpdateAdminOrderRequestDto.DocumentDto::id)
                .toList();

        List<Long> invalidDocumentIds = existingDocumentIds.stream()
                .filter(id -> !orderDocumentIds.contains(id))
                .toList();

        if (!invalidDocumentIds.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT,
                    "해당 주문(Order)에 속하지 않는 문서(Document)가 포함되었습니다: " + invalidDocumentIds);
        }

        // 기존 문서 업데이트
        Map<Long, Document> documentMap = documentRepository.findAllByIdsOrElseThrow(existingDocumentIds)
                .stream()
                .collect(Collectors.toMap(Document::getId, document -> document));

        existingDocuments.forEach(document -> {
            documentService.updateDocument(
                    documentMap.get(document.id()),
                    document.name(),
                    document.pageCount(),
                    document.recoveryOption(),
                    document.isOcrEnabled()
            );

            if (document.customRecoveryOptionPrice() != null) {
                documentMap.get(document.id()).updateCustomRecoveryOptionPrice(document.customRecoveryOptionPrice());
            }
            documentRepository.save(documentMap.get(document.id()));
        });

        // 신규 문서 생성
        newDocuments.forEach(document -> {
            Document doc = documentService.createDocument(
                    document.name(),
                    document.pageCount(),
                    document.recoveryOption(),
                    order,
                    order.getDocuments().get(0).getPricePolicy(),
                    document.isOcrEnabled()
            );
            if (document.customRecoveryOptionPrice() != null) {
                doc.updateCustomRecoveryOptionPrice(document.customRecoveryOptionPrice());
            }
            documentRepository.save(doc);
        });

        // 삭제 처리: DB에 존재하지만 요청에 포함되지 않은 문서는 삭제
        // 요청에 포함된 기존 문서의 ID 집합
        Set<Long> requestExistingIds = new HashSet<>(existingDocumentIds);
        // 주문에 속한 기존 문서 중 요청에 포함되지 않은 ID 찾기
        Set<Long> toDeleteIds = orderDocumentIds.stream()
                .filter(id -> !requestExistingIds.contains(id))
                .collect(Collectors.toSet());
        System.out.println("toDeleteIds = " + toDeleteIds);
        // 삭제 처리 (필요하다면 Order 엔티티에서도 해당 Document를 제거)
        order.getDocuments().removeIf(doc -> toDeleteIds.contains(doc.getId()));
        toDeleteIds.forEach(documentRepository::deleteByIdOrElseThrow);

        // 주문 정보 업데이트
        order.updateIsOneDayScan(requestDto.isOneDayScan());

        order.getDelivery().updateDeliveryPrice(requestDto.deliveryPrice());

        order.getDelivery().updateIsDeliveryFree(requestDto.isDeliveryFree());

        order.updateAdditionalDiscount(requestDto.additionalCouponDiscount());

        orderRepository.save(order);
    }
}
