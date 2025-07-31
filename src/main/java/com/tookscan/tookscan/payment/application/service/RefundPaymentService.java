package com.tookscan.tookscan.payment.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.dto.PaymentRefundDto;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.RestClientUtil;
import com.tookscan.tookscan.core.utility.TossPaymentUtil;
import com.tookscan.tookscan.order.domain.type.EOrderStatus;
import com.tookscan.tookscan.payment.application.usecase.RefundPaymentUseCase;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.type.EPaymentStatus;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundPaymentService implements RefundPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final TossPaymentUtil tossPaymentUtil;

    private final RestClientUtil restClientUtil;

    @Override
    @Transactional
    @BusinessLog(
        domain = "Payment",
        action = "refund payment",
        userType = "User" 
    )
    public void execute(Long paymentId) {

        Payment payment = paymentRepository.findByIdOrElseThrow(paymentId);

        String tossRefundApiUrl = tossPaymentUtil.getTossRefundRequestUrl(payment.getPaymentKey());

        HttpHeaders requestHeaders = tossPaymentUtil.getTossRefundRequestHeaders();

        String payload = tossPaymentUtil.createTossRefundRequestBody();

        PaymentRefundDto response = tossPaymentUtil.mapToPaymentRefundDto(restClientUtil.sendPost(tossRefundApiUrl, requestHeaders, payload));

        if (response.status().equals("CANCELED")){
            payment.updateStatus(EPaymentStatus.CANCELED);
            payment.getOrder().updateOrderStatus(EOrderStatus.CANCEL);

            paymentRepository.save(payment);
            
            LogContext.put("payment_id", payment.getId());
            LogContext.put("order_id", payment.getOrder().getId());
        } else {
            throw new RuntimeException("환불 실패");
        }
    }
}
