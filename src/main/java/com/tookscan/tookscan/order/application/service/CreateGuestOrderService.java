package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.address.domain.Address;
import com.tookscan.tookscan.address.domain.service.AddressService;
import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.order.application.dto.request.CreateGuestOrderRequestDto;
import com.tookscan.tookscan.order.application.dto.response.CreateGuestOrderResponseDto;
import com.tookscan.tookscan.order.application.usecase.CreateGuestOrderUseCase;
import com.tookscan.tookscan.order.domain.*;
import com.tookscan.tookscan.order.domain.service.*;
import com.tookscan.tookscan.order.domain.type.EDeliveryStatus;
import com.tookscan.tookscan.order.repository.*;
import com.tookscan.tookscan.security.domain.redis.AuthenticationCode;
import com.tookscan.tookscan.security.domain.service.AuthenticationCodeService;
import com.tookscan.tookscan.security.repository.AuthenticationCodeHistoryRepository;
import com.tookscan.tookscan.security.repository.AuthenticationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateGuestOrderService implements CreateGuestOrderUseCase {
    private final DeliveryRepository deliveryRepository;
    private final PricePolicyRepository pricePolicyRepository;
    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;
    private final InitialDocumentRepository initialDocumentRepository;
    private final AuthenticationCodeRepository authenticationCodeRepository;
    private final AuthenticationCodeHistoryRepository authenticationCodeHistoryRepository;
    private final CouponRepository couponRepository;

    private final OrderService orderService;
    private final AddressService addressService;
    private final DeliveryService deliveryService;
    private final DocumentService documentService;
    private final InitialDocumentService initialDocumentService;
    private final AuthenticationCodeService authenticationCodeService;
    private final CouponService couponService;

    private final KakaoMessageUtil kakaoMessageUtil;

    @Override
    @Transactional
    public CreateGuestOrderResponseDto execute(CreateGuestOrderRequestDto requestDto) {

        // 해당 번호에 관련된 인증번호 조회
        AuthenticationCode authenticationCode = authenticationCodeRepository.findByIdOrElseNull(requestDto.deliveryInfo().phoneNumber());

        // 인증번호 인증이 완료되었는지 확인
        authenticationCodeService.validateAuthenticationCode(authenticationCode);

        // 가격 정책 조회
        PricePolicy pricePolicy = pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(
                LocalDate.now(), LocalDate.now());

        Coupon coupon = null;

        // 쿠폰 조회
        if (requestDto.couponId() != null) {
            coupon = couponRepository.findByIdOrElseThrow(requestDto.couponId());
            couponService.validateCouponExpiration(coupon);
        }

        // 주소 정보 생성
        Address address = addressService.createAddress(
                requestDto.deliveryInfo().address().addressName(),
                requestDto.deliveryInfo().address().region1DepthName(),
                requestDto.deliveryInfo().address().region2DepthName(),
                requestDto.deliveryInfo().address().region3DepthName(),
                requestDto.deliveryInfo().address().region4DepthName(),
                requestDto.deliveryInfo().address().addressDetail(),
                requestDto.deliveryInfo().address().latitude(),
                requestDto.deliveryInfo().address().longitude()
        );

        // 배송 정보 생성
        Delivery delivery = deliveryService.createDelivery(
                requestDto.deliveryInfo().receiverName(),
                requestDto.deliveryInfo().phoneNumber(),
                requestDto.deliveryInfo().email(),
                EDeliveryStatus.POST_WAITING,
                requestDto.deliveryInfo().request(),
                address,
                pricePolicy.getDeliveryPrice()
        );
        deliveryRepository.save(delivery);

        // 주문 생성
        Order order = orderService.createOrder(null, false, delivery, coupon);
        orderRepository.save(order);

        // 쿠폰 적용
        if (coupon != null) {
            couponService.applyCoupon(coupon, order);
        }

        // 문서 생성
        requestDto.documents().forEach(doc -> {
            Document document = documentService.createDocument(
                    doc.name(),
                    doc.pagePrediction(),
                    doc.recoveryOption(),
                    order,
                    pricePolicy
            );
            order.getDocuments().add(document);
            documentRepository.save(document);
        });

        requestDto.documents().forEach(doc -> {
            InitialDocument initialDocument = initialDocumentService.createInitialDocument(
                    doc.name(),
                    doc.pagePrediction(),
                    doc.recoveryOption(),
                    order,
                    pricePolicy
            );
            initialDocumentRepository.save(initialDocument);
        });

        // 인증번호 삭제
        authenticationCodeRepository.deleteById(requestDto.deliveryInfo().phoneNumber());

        // 인증번호 발급 이력 삭제
        authenticationCodeHistoryRepository.deleteById(requestDto.deliveryInfo().phoneNumber());

        // 주문 접수 문자 발송
        kakaoMessageUtil.sendCreateOrderMessage(
                requestDto.deliveryInfo().receiverName(),
                requestDto.deliveryInfo().phoneNumber()
        );

        return CreateGuestOrderResponseDto.builder().orderNumber(order.getOrderNumber()).build();
    }
}
