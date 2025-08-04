package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.UsedCoupon;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsedCouponJpaRepository extends JpaRepository<UsedCoupon, Long> {


    @EntityGraph(attributePaths = {"issuedCoupon"})
    Optional<UsedCoupon> findWithIssuedCouponByOrderId(Long orderId);

    @Query("SELECT uc FROM UsedCoupon uc " +
            "JOIN FETCH uc.issuedCoupon ic " +
            "JOIN FETCH ic.couponTemplate ct " +
            "JOIN FETCH uc.order o " +
            "JOIN FETCH o.delivery d " +
            "JOIN FETCH o.documents doc " +
            "WHERE ct.id = :couponTemplateId ")
    List<UsedCoupon> findByCouponTemplateId(@Param("couponTemplateId") Long couponTemplateId);

    @Query("SELECT COUNT(uc) FROM UsedCoupon uc " +
            "JOIN  uc.issuedCoupon.couponTemplate ct " +
            "WHERE uc.user.id = :userId " +
            "AND ct.id = :couponTemplateId")
    int countByUserIdAndCouponTemplateId(@Param("userId") UUID userId, @Param("couponTemplateId") Long couponTemplateId);

    @Query("SELECT CASE WHEN COUNT(uc) > 0 THEN true ELSE false END " +
            "FROM UsedCoupon uc " +
            "JOIN uc.issuedCoupon.couponTemplate ct " +
            "WHERE ct.id = :couponTemplateId")
    boolean existsByCouponTemplateId(@Param("couponTemplateId") Long couponTemplateId);
}
