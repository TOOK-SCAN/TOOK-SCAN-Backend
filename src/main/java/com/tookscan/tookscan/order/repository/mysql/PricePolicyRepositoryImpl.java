package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
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
}
