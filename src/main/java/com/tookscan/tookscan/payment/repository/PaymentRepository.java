package com.tookscan.tookscan.payment.repository;

import com.tookscan.tookscan.payment.domain.Payment;

import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentRepository {
    Integer sumTotalAmountByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Integer> findMonthlyPaymentAmounts(LocalDateTime startDate, LocalDateTime endDate);

    Payment findByPaymentKeyOrElseThrow(String paymentKey);

    Payment saveAndReturn(Payment payment);

    Payment findByIdOrElseThrow(Long id);

    void save(Payment payment);
}
