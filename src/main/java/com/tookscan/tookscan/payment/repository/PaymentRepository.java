package com.tookscan.tookscan.payment.repository;

import com.tookscan.tookscan.payment.domain.Payment;

import java.time.LocalDateTime;

public interface PaymentRepository {
    Integer sumTotalAmountByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    java.util.Map<String, Integer> findMonthlyPaymentAmounts(LocalDateTime startDate, LocalDateTime endDate);
    Payment saveAndReturn(Payment payment);
    Payment findByIdOrElseThrow(Long id);
    void save(Payment payment);
}
