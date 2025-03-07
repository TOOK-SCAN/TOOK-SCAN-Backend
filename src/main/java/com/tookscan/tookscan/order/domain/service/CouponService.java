package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import org.springframework.stereotype.Service;

@Service
public class CouponService {
    public void validateCouponExpiration(Coupon coupon) {
        if (!coupon.isAvailable()) {
            throw new CommonException(ErrorCode.EXPIRED_COUPON);
        }
    }

    public void applyCoupon(Coupon coupon, Order order) {
        if (coupon.getType() == ECouponType.DELIVERY_PRICE) {
            order.getDelivery().updateDeliveryPrice(0);
        }
    }
}
