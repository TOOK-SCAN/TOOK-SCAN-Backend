package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.ExcelUtils;
import com.tookscan.tookscan.order.application.usecase.ExportAdminIssuedCouponExcelUseCase;
import com.tookscan.tookscan.order.domain.IssuedCoupon;
import com.tookscan.tookscan.order.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportAdminIssuedCouponExcelService implements ExportAdminIssuedCouponExcelUseCase {

    private final IssuedCouponRepository issuedCouponRepository;
    private final ExcelUtils excelUtils;

    @Override
    @Transactional(readOnly = true)
    @BusinessLog(
            domain = "IssuedCoupon",
            action = "export issued coupons to excel",
            userType = "Admin"
    )
    public byte[] execute(Long couponTemplateId) {
        List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByCouponTemplateId(couponTemplateId);
        if (issuedCoupons.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_ISSUED_COUPON);
        }

        return excelUtils.writeIssuedCoupons(issuedCoupons);
    }
}
