package com.tookscan.tookscan.payment.repository;

import com.tookscan.tookscan.payment.domain.Payment;

import java.time.LocalDateTime;

public interface PaymentRepository {
    Integer sumTotalAmountByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Payment saveAndReturn(Payment payment);
    Payment findByOrderNumberOrElseThrow(String orderNumber);
}
