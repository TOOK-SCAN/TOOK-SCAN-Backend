package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.CouponTemplate;

public interface CouponTemplateRepository {

    CouponTemplate findByIdOrElseThrow(Long id);

    void save(CouponTemplate couponTemplate);
}
