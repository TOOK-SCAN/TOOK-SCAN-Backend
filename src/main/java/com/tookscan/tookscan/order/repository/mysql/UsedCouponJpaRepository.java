package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.UsedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UsedCouponJpaRepository extends JpaRepository<UsedCoupon, Long> {


    @Query("SELECT COUNT(uc) FROM UsedCoupon uc " +
            "JOIN  uc.issuedCoupon.couponTemplate ct " +
            "WHERE uc.user.id = :userId " +
            "AND ct.id = :couponTemplateId")
    int countByUserIdAndCouponTemplateId(@Param("userId") UUID userId, @Param("couponTemplateId") Long couponTemplateId);
}
