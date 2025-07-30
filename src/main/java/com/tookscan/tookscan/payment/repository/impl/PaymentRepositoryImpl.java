package com.tookscan.tookscan.payment.repository.impl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.payment.domain.Payment;
import com.tookscan.tookscan.payment.domain.QPayment;
import com.tookscan.tookscan.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import com.tookscan.tookscan.payment.repository.mysql.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Integer sumTotalAmountByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentJpaRepository.sumTotalAmountByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Map<String, Integer> findMonthlyPaymentAmounts(LocalDateTime startDate, LocalDateTime endDate) {
        QPayment payment = QPayment.payment;
        
        return jpaQueryFactory
                .select(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", payment.createdAt),
                        payment.totalAmount.sum().intValue()
                )
                .from(payment)
                .where(payment.createdAt.between(startDate, endDate))
                .groupBy(
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", payment.createdAt)
                )
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class),
                        tuple -> tuple.get(1, Integer.class)
                ));
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
