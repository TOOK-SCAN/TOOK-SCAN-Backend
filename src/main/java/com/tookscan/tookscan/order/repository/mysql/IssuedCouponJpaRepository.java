package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.IssuedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

    Optional<IssuedCoupon> findByCode(String couponCode);

    Page<IssuedCoupon> findByCouponTemplateId(Long couponTemplateId, Pageable pageable);

    boolean existsByCode(String couponCode);
}
