package com.tookscan.tookscan.order.repository.impl;

import com.tookscan.tookscan.order.domain.UsedCoupon;
import com.tookscan.tookscan.order.repository.UsedCouponRepository;
import com.tookscan.tookscan.order.repository.mysql.UsedCouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UsedCouponRepositoryImpl implements UsedCouponRepository {

    private final UsedCouponJpaRepository usedCouponJpaRepository;

    @Override
    public UsedCoupon findByIdOrElseThrow(Long id) {
        return usedCouponJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UsedCoupon not found with ID: " + id));
    }

    @Override
    public int countByUserIdAndCouponTemplateId(UUID userId, Long couponTemplateId) {
        return usedCouponJpaRepository.countByUserIdAndCouponTemplateId(userId, couponTemplateId);
    }

    @Override
    public void save(UsedCoupon usedCoupon) {
        usedCouponJpaRepository.save(usedCoupon);
    }
}
