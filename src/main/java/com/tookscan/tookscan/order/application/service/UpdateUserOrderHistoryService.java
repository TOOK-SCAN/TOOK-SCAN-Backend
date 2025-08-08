package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.address.domain.service.AddressService;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.UpdateUserOrderHistoryUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.DocumentService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderHistoryRequestDto;
import com.tookscan.tookscan.order.presentation.dto.request.UpdateUserOrderHistoryRequestDto.HistoryRequestDocument;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserOrderHistoryService implements UpdateUserOrderHistoryUseCase {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;
    private final UsedCouponRepository usedCouponRepository;

    private final PricePolicyRepository pricePolicyRepository;

    private final OrderService orderService;
    private final DocumentService documentService;
    private final AddressService addressService;
    private final DeliveryService deliveryService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "update order history",
        userType = "User"
    )
    public void execute(UUID accountId, Long orderId, UpdateUserOrderHistoryRequestDto requestDto) {
        User user = userRepository.findByIdOrElseThrow(accountId);
        Order order = orderRepository.findByIdOrElseThrow(orderId);
        orderService.validateOrderUser(order, user);
        orderService.validateUpdatableOrder(order);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());
        Set<Long> orderDocumentIds = order.getDocuments().stream()
                .map(Document::getId)
                .collect(Collectors.toSet());

        // 요청으로 들어온 문서들을 신규 문서(id == null)와 기존 문서(id != null)로 분리
        List<HistoryRequestDocument> newDocuments = requestDto.documents().stream()
                .filter(doc -> doc.id() == null)
                .toList();

        List<HistoryRequestDocument> existingDocuments = requestDto.documents().stream()
                .filter(doc -> doc.id() != null)
                .toList();

        // 기존 문서의 경우, 해당 주문에 속한 문서인지 검증
        List<Long> existingDocumentIds = existingDocuments.stream()
                .map(HistoryRequestDocument::id)
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
                    document.isOcrEnabled(),
                    pricePolicy.getAdditionalPriceForOcr()
            );

            documentRepository.save(documentMap.get(document.id()));
        });

        // 신규 문서 생성
        newDocuments.forEach(document -> {
            Document doc = documentService.createDocument(
                    document.name(),
                    document.pageCount(),
                    document.recoveryOption(),
                    order,
                    pricePolicy.getCuttingPrice(),
                    pricePolicy.getDefaultPricePerPage(),
                    pricePolicy.getAdditionalPriceForOcr(),
                    document.isOcrEnabled()
            );
            documentRepository.save(doc);
        });

        // 삭제 처리: DB에 존재하지만 요청에 포함되지 않은 문서는 삭제
        // 요청에 포함된 기존 문서의 ID 집합
        Set<Long> requestExistingIds = new HashSet<>(existingDocumentIds);
        // 주문에 속한 기존 문서 중 요청에 포함되지 않은 ID 찾기
        Set<Long> toDeleteIds = orderDocumentIds.stream()
                .filter(id -> !requestExistingIds.contains(id))
                .collect(Collectors.toSet());

        // 삭제 처리 (필요하다면 Order 엔티티에서도 해당 Document를 제거)
        order.getDocuments().removeIf(doc -> toDeleteIds.contains(doc.getId()));
        toDeleteIds.forEach(documentRepository::deleteByIdOrElseThrow);

        if (requestDto.address() != null) {
            Address address = addressService.createAddress(
                    requestDto.address().addressName(),
                    requestDto.address().region1DepthName(),
                    requestDto.address().region2DepthName(),
                    requestDto.address().region3DepthName(),
                    requestDto.address().region4DepthName(),
                    requestDto.address().addressDetail(),
                    requestDto.address().zoneCode(),
                    requestDto.address().latitude(),
                    requestDto.address().longitude()
            );
            order.getDelivery().updateAddress(address);
            order.getDelivery().updateRequest(requestDto.deliveryRequest());
        }

        // 주문 정보 업데이트
        orderService.updateIsOneDayScan(order, requestDto.isOneDayScan());

        if (order.isDelivery()) {
            deliveryService.updateDeliveryPrice(order.getDelivery(), pricePolicy.getDeliveryPrice());
        } else {
            deliveryService.updateDeliveryPrice(order.getDelivery(), 0);
        }

        order.calculateTotalAmount();
        orderRepository.save(order);

        if (order.getUsedCoupon() != null) {
            UsedCoupon usedCoupon = usedCouponRepository.findWithIssuedCouponByOrderIdOrElseThrow(order.getId());
            IssuedCoupon issuedCoupon = usedCoupon.getIssuedCoupon();

            this.validateCouponExpiration(issuedCoupon, order.getTotalAmountWithoutCouponAndDelivery());
        }

        LogContext.put("order_id", orderId);
        LogContext.put("user_id", accountId);
        LogContext.put("updated_documents_count", existingDocuments.size());
        LogContext.put("new_documents_count", newDocuments.size());
        LogContext.put("deleted_documents_count", toDeleteIds.size());

    }

    public void validateCouponExpiration(IssuedCoupon issuedCoupon, Integer totalAmount) {

        // 쿠폰이 최소 주문 금액을 만족하는지 확인
        if (issuedCoupon.getCouponTemplate().getMinOrderPrice() != null && issuedCoupon.getCouponTemplate().getMinOrderPrice() > totalAmount) {
            throw new CommonException(ErrorCode.NOT_ENOUGH_ORDER_PRICE_FOR_COUPON);
        }
    }
}
