package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.PricePolicy;
import java.time.LocalDate;
import java.util.Optional;

public interface PricePolicyRepository {
    PricePolicy findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrElseThrow(LocalDate now1, LocalDate now2);

    Optional<PricePolicy> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate now1, LocalDate now2);

    void save(PricePolicy pricePolicy);
}
