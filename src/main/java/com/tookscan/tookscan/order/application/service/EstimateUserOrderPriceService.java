package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.order.application.usecase.EstimateUserOrderPriceUseCase;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.service.CouponService;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.DocumentService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.presentation.dto.request.EstimateUserOrderPriceRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.EstimateUserOrderPriceResponseDto;
import com.tookscan.tookscan.order.repository.CouponRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstimateUserOrderPriceService implements EstimateUserOrderPriceUseCase {

    private final PricePolicyRepository pricePolicyRepository;
    private final CouponRepository couponRepository;

    private final OrderService orderService;
    private final DocumentService documentService;
    private final DeliveryService deliveryService;
    private final CouponService couponService;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "estimate order price",
        userType = "User"
    )
    public EstimateUserOrderPriceResponseDto execute(EstimateUserOrderPriceRequestDto requestDto) {
        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());

        Coupon coupon = null;

        // 쿠폰 조회
        if (requestDto.couponId() != null) {
            coupon = couponRepository.findByIdOrElseThrow(requestDto.couponId());
        }
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
        Order order = orderService.createOrder(user, delivery, coupon, requestDto.isOneDayScan(),
                pricePolicy.getAdditionalPriceForOneDayScan());

        // 쿠폰 적용
        if (coupon != null) {
            couponService.applyCoupon(coupon, order);
        }

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

        orderService.calculateTotalAmount(order);
        
        LogContext.put("estimated_total_amount", order.getTotalAmount());

        return EstimateUserOrderPriceResponseDto.of(order, unCheckedDocuments);
    }
}
