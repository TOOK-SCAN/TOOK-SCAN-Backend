package com.tookscan.tookscan.payment.application.service;

import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.service.OrderService;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.payment.application.usecase.CreatePaymentUseCase;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.type.EPaymentStatus;
import com.tookscan.tookscan.payment.presentation.dto.request.CreatePaymentRequestDto;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePaymentService implements CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    @Override
    @Transactional
    public void execute(CreatePaymentRequestDto requestDto) {
        Order order = orderRepository.findByIdOrElseThrow(requestDto.orderId());

        Payment payment = Payment.builder()
                .paymentKey("temp")
                .type("temp")
                .method(requestDto.method())
                .totalAmount(requestDto.totalAmount())
                .status(EPaymentStatus.DONE)
                .requestedAt(requestDto.approvedAt())
                .approvedAt(requestDto.approvedAt())
                .order(order)
                .build();

        orderService.finishPayment(order, payment);

        // 결제 정보 저장
        paymentRepository.save(payment);
    }
}
