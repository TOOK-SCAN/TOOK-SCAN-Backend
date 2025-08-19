package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.IssuedCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

    @EntityGraph(attributePaths = {"usedCoupons"})
    Page<IssuedCoupon> findByCouponTemplateId(Long couponTemplateId, Pageable pageable);

    @EntityGraph(attributePaths = {"usedCoupons"})
    List<IssuedCoupon> findByCouponTemplateId(Long couponTemplateId);

    @EntityGraph(attributePaths = {"couponTemplate"})
    Optional<IssuedCoupon> findWithCouponTemplateByCode(String couponCode);

    boolean existsByCode(String couponCode);

    int countByCouponTemplateId(Long couponTemplateId);
}
