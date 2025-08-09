package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.address.domain.service.AddressService;
import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.TsidFactory;
import com.tookscan.tookscan.message.domain.event.CreateOrderMessageEvent;
import com.tookscan.tookscan.order.application.usecase.CreateUserOrderUseCase;
import com.tookscan.tookscan.order.domain.Delivery;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.domain.service.DeliveryService;
import com.tookscan.tookscan.order.domain.service.DocumentService;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.order.domain.type.ERecoveryOption;
import com.tookscan.tookscan.order.presentation.dto.request.CreateUserOrderRequestDto;
import com.tookscan.tookscan.order.presentation.dto.response.CreateUserOrderResponseDto;
import com.tookscan.tookscan.order.repository.DeliveryRepository;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.IssuedCouponRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final IssuedCouponRepository issuedCouponRepository;
    private final UsedCouponRepository usedCouponRepository;

    private final OrderService orderService;
    private final DocumentService documentService;
    private final AddressService addressService;
    private final DeliveryService deliveryService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private static final Integer DELIVERY_EXPIRATION_PERIOD = 14;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Order",
        action = "create order",
        userType = "User"
    )
    public CreateUserOrderResponseDto execute(UUID accountId, CreateUserOrderRequestDto requestDto) {
        // 계정 조회
        User user = userRepository.findByIdOrElseThrow(accountId);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());

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
        String orderNumber = TsidFactory.getFactory().generate().toString();
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .orderStatus(EOrderStatus.APPLY_COMPLETED)
                .deliveryExpirationDate(
                        LocalDateTime.now().plusDays(DELIVERY_EXPIRATION_PERIOD).withHour(23).withMinute(59)
                                .withSecond(59).withNano(0))
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

        orderRepository.save(order);

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

        order.calculateTotalAmount();

        UsedCoupon usedCoupon = null;

        // 사용 요청받은 쿠폰 조회
        if (requestDto.couponId() != null) {
            IssuedCoupon issuedCoupon = issuedCouponRepository.findByIdOrElseThrow(requestDto.couponId());

            // 쿠폰 유효성 검증
            this.validateCouponExpiration(accountId, issuedCoupon, order.getTotalAmountWithoutCouponAndDelivery());

            // 쿠폰 적용
            usedCoupon = UsedCoupon.builder()
                    .issuedCoupon(issuedCoupon)
                    .user(user)
                    .initialOrder(null)
                    .order(order)
                    .build();
            usedCouponRepository.save(usedCoupon);

            issuedCoupon.useCoupon();
            issuedCouponRepository.save(issuedCoupon);
        }

        applicationEventPublisher.publishEvent(
                CreateOrderMessageEvent.of(
                        user.getName(),
                        user.getPhoneNumber(),
                        order.getDocumentsDescription(),
                        order.getDelivery().getPhoneNumber()
                )
        );

        LogContext.put("order_id", order.getId());
        LogContext.put("order_number", order.getOrderNumber());

        return CreateUserOrderResponseDto.builder().orderNumber(order.getOrderNumber())
                .orderId(order.getId().toString()).build();
    }


    public void validateCouponExpiration(UUID accountId, IssuedCoupon issuedCoupon, Integer totalAmount) {

        // 쿠폰의 사용 기간 확인
        if (issuedCoupon.getCouponTemplate().getStartDateTime() != null
                && issuedCoupon.getCouponTemplate().getEndDateTime() != null) {
            if (issuedCoupon.getCouponTemplate().getStartDateTime().isAfter(LocalDate.now().atStartOfDay())) {
                throw new CommonException(ErrorCode.NOT_AVAILABLE_COUPON);
            }
            if (issuedCoupon.getCouponTemplate().getEndDateTime().isBefore(LocalDate.now().atStartOfDay())) {
                throw new CommonException(ErrorCode.NOT_AVAILABLE_COUPON);
            }
        }

        // 한 사용자당 쿠폰 사용 횟수를 넘겼는지 확인
        if (issuedCoupon.getCouponTemplate().getMaxUsedPerUserCount() != null &&
                usedCouponRepository.countByUserIdAndCouponTemplateId(accountId,
                        issuedCoupon.getCouponTemplate().getId()) > issuedCoupon.getCouponTemplate()
                        .getMaxUsedPerUserCount()) {
            throw new CommonException(ErrorCode.EXCEEDED_MAX_USED_COUPON_PER_USER);

        }

        // 전체 사용자의 쿠폰 사용 횟수를 넘겼는지 확인
        if (issuedCoupon.getMaxUsedCount() != null && issuedCoupon.getUsedCount() >= issuedCoupon.getMaxUsedCount()) {
            throw new CommonException(ErrorCode.EXCEEDED_MAX_USED_COUPON);
        }

        // 쿠폰이 최소 주문 금액을 만족하는지 확인
        if (issuedCoupon.getCouponTemplate().getMinOrderPrice() != null
                && issuedCoupon.getCouponTemplate().getMinOrderPrice() > totalAmount) {
            throw new CommonException(ErrorCode.NOT_ENOUGH_ORDER_PRICE_FOR_COUPON);
        }
    }
}
