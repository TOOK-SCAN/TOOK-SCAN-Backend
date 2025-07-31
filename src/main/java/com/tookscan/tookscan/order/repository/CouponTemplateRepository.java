package com.tookscan.tookscan.order.repository;

import com.tookscan.tookscan.order.domain.CouponTemplate;
import com.tookscan.tookscan.order.domain.type.ECouponFormat;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponTemplateRepository {

    CouponTemplate findByIdOrElseThrow(Long id);

    Page<Long> findCouponTemplatesByFormatAndType(
            ECouponFormat format,
            ECouponType type,
            String status,
            Pageable pageable
    );

    List<CouponTemplate> findAllWithIssuedCouponsByIdIn(List<Long> ids);

    void save(CouponTemplate couponTemplate);

    void deleteById(Long id);
}
