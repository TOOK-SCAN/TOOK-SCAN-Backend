package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.core.utility.TsidFactory;
import com.tookscan.tookscan.order.application.usecase.EstimateUserOrderPriceUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.DocumentService;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.presentation.dto.request.EstimateUserOrderPriceRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.EstimateUserOrderPriceResponseDto;
import com.tookscan.tookscan.order.repository.IssuedCouponRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstimateUserOrderPriceService implements EstimateUserOrderPriceUseCase {

    private final PricePolicyRepository pricePolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    private final DocumentService documentService;
    private final DeliveryService deliveryService;

    private static final Integer DELIVERY_EXPIRATION_PERIOD = 14;

    @Override
    @Transactional(readOnly = true)
    public EstimateUserOrderPriceResponseDto execute(EstimateUserOrderPriceRequestDto requestDto) {
        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());

        User user = null;
        Address address = null;

        // 배송 정보 생성
        Integer deliveryPrice = 0;
        if (requestDto.documents().stream()
                .anyMatch(document -> document.recoveryOption() != ERecoveryOption.DISCARD)) {
            deliveryPrice =
                    requestDto.deliveryPrice() != null ? requestDto.deliveryPrice() : pricePolicy.getDeliveryPrice();
        }
        Delivery delivery = deliveryService.createDelivery(
                "",
                "",
                "",
                EDeliveryStatus.POST_WAITING,
                "",
                address,
                deliveryPrice
        );

        // 주문 생성
        String orderNumber = TsidFactory.getFactory().generate().toString();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .orderStatus(EOrderStatus.APPLY_COMPLETED)
                .deliveryExpirationDate(LocalDateTime.now().plusDays(DELIVERY_EXPIRATION_PERIOD + 1).withHour(0).withMinute(0).withSecond(0).withNano(0))
                .scanCopyrightComplianceAgreed(LocalDateTime.now())
                .illegalDistributionProhibitionAgreed(LocalDateTime.now())
                .cuttingAgreed(LocalDateTime.now())
                .serviceProvisionPeriodAcknowledged(LocalDateTime.now())
                .user(user)
                .delivery(delivery)
                .isOneDayScan(requestDto.isOneDayScan())
                .isAsInProgress(false)
                .additionalPriceForOneDayScan(pricePolicy.getAdditionalPriceForOneDayScan())
                .totalAmount(0)
                .build();

        UsedCoupon usedCoupon = null;

        // 사용 요청받은 쿠폰 조회
        if (requestDto.couponId() != null) {
            IssuedCoupon issuedCoupon = issuedCouponRepository.findByIdOrElseThrow(requestDto.couponId());

            // 쿠폰 적용
            usedCoupon = UsedCoupon.builder()
                    .issuedCoupon(issuedCoupon)
                    .user(user)
                    .initialOrder(null)
                    .order(order)
                    .build();

            issuedCoupon.useCoupon();
        }

        // 쿠폰 저장
        order.updateUsedCoupon(usedCoupon);

        // 문서 생성
        List<Document> unCheckedDocuments = new ArrayList<>();
        requestDto.documents().forEach(doc -> {
            Document document = documentService.createDocument(
                    doc.name(),
                    doc.pageCount(),
                    doc.recoveryOption(),
                    order,
                    pricePolicy.getCuttingPrice(),
                    pricePolicy.getDefaultPricePerPage(),
                    pricePolicy.getAdditionalPriceForOcr(),
                    doc.isOcrEnabled()
            );
            if (doc.recoveryOptionPrice() != null) {
                documentService.updateRecoveryOptionPrice(document, doc.recoveryOptionPrice());
            }
            if (doc.isChecked()) {
                order.getDocuments().add(document);
            } else {
                unCheckedDocuments.add(document);
            }
        });

        order.calculateTotalAmount();

        return EstimateUserOrderPriceResponseDto.of(order, unCheckedDocuments);
    }
}
