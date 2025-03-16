package com.tookscan.tookscan.payment.repository.mysql;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Integer sumTotalAmountByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentJpaRepository.sumTotalAmountByCreatedAtBetween(startDate, endDate);
    }
    @Override
    public Payment saveAndReturn(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findByIdOrElseThrow(Long id) {
        return paymentJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE, "Payment not found"));
    }

    @Override
    public void save(Payment payment) {
        paymentJpaRepository.save(payment);
    }
}
