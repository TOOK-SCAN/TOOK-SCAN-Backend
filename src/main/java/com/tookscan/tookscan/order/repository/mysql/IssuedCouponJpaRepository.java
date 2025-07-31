package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

    Optional<IssuedCoupon> findByCode(String couponCode);

    boolean existsByCode(String couponCode);
}
