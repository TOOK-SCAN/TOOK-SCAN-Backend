package com.tookscan.tookscan.payment.application.service;

import com.tookscan.tookscan.core.dto.PaymentDto;
import com.tookscan.tookscan.core.utility.RestClientUtil;
import com.tookscan.tookscan.core.utility.TossPaymentUtil;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.payment.application.dto.request.ConfirmPaymentRequestDto;
import com.tookscan.tookscan.payment.application.usecase.ConfirmPaymentUseCase;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.service.PaymentService;
import com.tookscan.tookscan.payment.domain.type.EEasyPaymentProvider;
import com.tookscan.tookscan.payment.domain.type.EPaymentMethod;
import com.tookscan.tookscan.payment.domain.type.EPaymentStatus;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmPaymentService implements ConfirmPaymentUseCase {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private final PaymentService paymentService;

    private final TossPaymentUtil tossPaymentUtil;
    private final RestClientUtil restClientUtil;

    @Override
    @Transactional
    public void execute(ConfirmPaymentRequestDto requestDto) {

        String tossConfirmApiUrl = tossPaymentUtil.getTossConfirmRequestUrl();

        HttpHeaders requestHeaders = tossPaymentUtil.getTossConfirmRequestHeaders();

        String payload = tossPaymentUtil.createTossConfirmRequestBody(
                requestDto.paymentKey(),
                requestDto.orderNumber(),
                requestDto.amount()
        );

        PaymentDto response = tossPaymentUtil.mapToPaymentDto(restClientUtil.sendPost(tossConfirmApiUrl, requestHeaders, payload));

        Order order = orderRepository.findByOrderNumberOrElseThrow(requestDto.orderNumber());

        Payment payment = paymentService.createPayment(
                response.paymentKey(),
                response.type(),
                response.method() != null ? EPaymentMethod.fromString(response.method()) : null,
                response.totalAmount(),
                EPaymentStatus.fromString(response.status()),
                OffsetDateTime.parse(response.requestedAt()).toLocalDateTime(),
                response.approvedAt() != null ? OffsetDateTime.parse(response.approvedAt()).toLocalDateTime() : null,
                response.easyPay() != null ? EEasyPaymentProvider.fromString(response.easyPay().provider()) : null,
                order
        );

        payment = paymentRepository.saveAndReturn(payment);

        // 결제 완료 시 주문 상태 변경
        if (payment.getStatus().equals(EPaymentStatus.DONE)) {
            order.finishPayment(payment);
            orderRepository.save(order);
        }

    }
}
