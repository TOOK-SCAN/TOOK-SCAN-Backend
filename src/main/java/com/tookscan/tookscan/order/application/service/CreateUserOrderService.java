package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.address.domain.service.AddressService;
import com.tookscan.tookscan.message.domain.event.CreateOrderMessageEvent;
import com.tookscan.tookscan.order.application.usecase.CreateUserOrderUseCase;
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
import com.tookscan.tookscan.order.presentation.dto.request.CreateUserOrderRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.CreateUserOrderResponseDto;
import com.tookscan.tookscan.order.repository.CouponRepository;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserOrderService implements CreateUserOrderUseCase {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;
    private final DeliveryRepository deliveryRepository;
    private final PricePolicyRepository pricePolicyRepository;
    private final CouponRepository couponRepository;

    private final OrderService orderService;
    private final DocumentService documentService;
    private final AddressService addressService;
    private final DeliveryService deliveryService;
    private final CouponService couponService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public CreateUserOrderResponseDto execute(UUID accountId, CreateUserOrderRequestDto requestDto) {
        // 계정 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());

        Coupon coupon = null;

        // 쿠폰 조회
        if (requestDto.couponId() != null) {
            coupon = couponRepository.findByIdOrElseThrow(requestDto.couponId());
            couponService.validateCouponExpiration(coupon);
        }

        Address address = Optional.ofNullable(requestDto.deliveryInfo().address())
                .map(addr -> addressService.createAddress(
                        addr.addressName(),
                        addr.region1DepthName(),
                        addr.region2DepthName(),
                        addr.region3DepthName(),
                        addr.region4DepthName(),
                        addr.addressDetail(),
                        addr.zoneCode(),
                        addr.latitude(),
                        addr.longitude()
                ))
                .orElse(null);

        // 배송 정보 생성
        Integer deliveryPrice = 0;
        if (requestDto.documents().stream()
                .anyMatch(document -> document.recoveryOption() != ERecoveryOption.DISCARD)) {
            deliveryPrice = pricePolicy.getDeliveryPrice();
        }
        Delivery delivery = deliveryService.createDelivery(
                requestDto.deliveryInfo().receiverName(),
                requestDto.deliveryInfo().phoneNumber(),
                requestDto.deliveryInfo().email(),
                EDeliveryStatus.POST_WAITING,
                requestDto.deliveryInfo().request(),
                address,
                deliveryPrice
        );
        deliveryRepository.save(delivery);

        // 주문 생성
        Order order = orderService.createOrder(user, delivery, coupon, requestDto.isOneDayScan(),
                pricePolicy.getAdditionalPriceForOneDayScan());
        orderRepository.save(order);

        // 쿠폰 적용
        if (coupon != null) {
            couponService.applyCoupon(coupon, order);
        }

        // 문서 생성
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
                    order.getDocuments().add(document);
                    documentRepository.save(document);
        });

        orderService.calculateTotalAmount(order);

        applicationEventPublisher.publishEvent(
                CreateOrderMessageEvent.of(
                        user.getName(),
                        user.getPhoneNumber(),
                        order.getDocumentsDescription(),
                        order.getDelivery().getPhoneNumber()
                )
        );

        return CreateUserOrderResponseDto.builder().orderNumber(order.getOrderNumber())
                .orderId(order.getId().toString()).build();
    }

}
