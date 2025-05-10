package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
import java.util.Optional;

import com.tookscan.tookscan.order.repository.mysql.PricePolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PricePolicyRepositoryImpl implements PricePolicyRepository {
    private final PricePolicyJpaRepository pricePolicyJpaRepository;

    @Override
    public PricePolicy findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(LocalDate now1,
                                                                                         LocalDate now2) {
        return pricePolicyJpaRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(now1, now2)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PRICE_POLICY));
    }

    @Override
    public Optional<PricePolicy> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate now1,
                                                                                                  LocalDate now2) {
        return pricePolicyJpaRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(now1, now2);
    }

    @Override
    public void save(PricePolicy pricePolicy) {
        pricePolicyJpaRepository.save(pricePolicy);
    }
}
