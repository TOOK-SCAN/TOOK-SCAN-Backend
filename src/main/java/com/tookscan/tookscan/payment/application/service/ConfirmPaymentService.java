package com.tookscan.tookscan.payment.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.dto.PaymentDto;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.KakaoMessageUtil;
import com.tookscan.tookscan.core.utility.RestClientUtil;
import com.tookscan.tookscan.core.utility.TossPaymentUtil;
import com.tookscan.tookscan.message.domain.event.RequestScanMessageEvent;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.payment.application.usecase.ConfirmPaymentUseCase;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.service.PaymentService;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import com.tookscan.tookscan.payment.domain.type.EPaymentStatus;
import com.tookscan.tookscan.payment.presentation.dto.request.ConfirmPaymentRequestDto;
import com.tookscan.tookscan.payment.presentation.dto.response.ConfirmPaymentResponseDto;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmPaymentService implements ConfirmPaymentUseCase {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private final PaymentService paymentService;
    private final OrderService orderService;

    private final TossPaymentUtil tossPaymentUtil;
    private final RestClientUtil restClientUtil;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Payment",
        action = "confirm payment",
        userType = "User"
    )
    public ConfirmPaymentResponseDto execute(ConfirmPaymentRequestDto requestDto) {

        String tossConfirmApiUrl = tossPaymentUtil.getTossConfirmRequestUrl();

        HttpHeaders requestHeaders = tossPaymentUtil.getTossConfirmRequestHeaders();

        String payload = tossPaymentUtil.createTossConfirmRequestBody(
                requestDto.paymentKey(),
                requestDto.orderNumber(),
                requestDto.amount()
        );

        PaymentDto response;

        try {
            response = tossPaymentUtil.mapToPaymentDto(restClientUtil.sendPost(tossConfirmApiUrl, requestHeaders, payload));
        } catch (CommonException e) {

            if (e.getErrorCode().equals(ErrorCode.ALREADY_PROCESSED_PAYMENT)) {
                Payment payment = paymentRepository.findByPaymentKeyOrElseThrow(requestDto.paymentKey());

                Order order = orderRepository.findWithDeliveryByOrderNumberOrElseThrow(requestDto.orderNumber());

                // 이미 처리된 결제의 경우, 결제 정보와 주문 정보를 반환
                return ConfirmPaymentResponseDto.of(
                        true,
                        order.getOrderNumber(),
                        payment.getApprovedAt() != null ? payment.getApprovedAt().toString() : null,
                        payment.getMethod() != null ? payment.getMethod() : null,
                        payment.getEasyPaymentProvider() != null ? payment.getEasyPaymentProvider() : null,
                        payment.getTotalAmount(),
                        null
                );
            }

            String tossInfoApiUrl = tossPaymentUtil.getTossInfoRequestUrl(requestDto.paymentKey());
            HttpHeaders infoHeaders = tossPaymentUtil.getTossInfoRequestHeaders();
            response = tossPaymentUtil.mapToPaymentDto(restClientUtil.sendGet(tossInfoApiUrl, infoHeaders));
            
            LogContext.put("payment_confirm_failed", true);
            LogContext.put("payment_key", requestDto.paymentKey());
            LogContext.put("error_message", e.getMessage());

            return ConfirmPaymentResponseDto.of(
                    false,
                    response.orderId(),
                    null,
                    response.method() != null ? EPaymentMethod.fromString(response.method()) : null,
                    response.easyPay() != null ? EEasyPaymentProvider.fromString(response.easyPay().provider()) : null,
                    response.totalAmount(),
                    e.getMessage()
            );
        }

        Order order = orderRepository.findWithDeliveryByOrderNumberOrElseThrow(requestDto.orderNumber());

        Payment payment = paymentService.createPayment(
                response.paymentKey(),
                response.type(),
                response.method() != null ? EPaymentMethod.fromString(response.method()) : null,
                response.totalAmount(),
                EPaymentStatus.fromString(response.status()),
                OffsetDateTime.parse(response.requestedAt()).toLocalDateTime(),
                response.approvedAt() != null ? OffsetDateTime.parse(response.approvedAt()).toLocalDateTime() : null,
                response.easyPay() != null ? EEasyPaymentProvider.fromString(response.easyPay().provider()) : null,
                order,
                response.receipt() != null && response.receipt().url() != null ? response.receipt().url() : null
        );

        payment = paymentRepository.saveAndReturn(payment);

        // 결제 완료 시 주문 상태 변경
        if (payment.getStatus().equals(EPaymentStatus.DONE)) {
            orderService.finishPayment(order, payment);
            orderRepository.save(order);

            // 스캔 요청 메시지 이벤트 발행
            applicationEventPublisher.publishEvent(
                    RequestScanMessageEvent.of(
                            order.getDocumentsDescription(),
                            order.getId(),
                            order.getDelivery().getEmail(),
                            order.getDelivery().getPhoneNumber()
                    )
            );
        }

        LogContext.put("payment_id", payment.getId());
        LogContext.put("order_id", order.getId());
        LogContext.put("payment_status", payment.getStatus().name());

        return ConfirmPaymentResponseDto.of(
                true,
                order.getOrderNumber(),
                payment.getApprovedAt() != null ? payment.getApprovedAt().toString() : null,
                payment.getMethod() != null ? payment.getMethod() : null,
                payment.getEasyPaymentProvider() != null ? payment.getEasyPaymentProvider() : null,
                payment.getTotalAmount(),
                null
        );
    }
}
