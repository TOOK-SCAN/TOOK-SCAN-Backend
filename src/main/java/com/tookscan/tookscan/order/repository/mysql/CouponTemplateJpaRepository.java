package com.tookscan.tookscan.order.repository.mysql;

import com.tookscan.tookscan.order.domain.CouponTemplate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponTemplateJpaRepository extends JpaRepository<CouponTemplate, Long> {

    @EntityGraph(attributePaths = {"issuedCoupons"})
    List<CouponTemplate> findAllWithIssuedCouponsByIdIn(List<Long> ids);
}
