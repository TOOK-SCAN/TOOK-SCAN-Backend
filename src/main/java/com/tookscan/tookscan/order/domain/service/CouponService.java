package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Coupon;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.type.ECouponType;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class CouponService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public Coupon createCoupon(String name, String code, String description, ECouponType type, Integer discountPrice,
                               Integer discountPercent, LocalDateTime startDateTime, LocalDateTime endDateTime,
                               boolean isPossibleDuplicatedApply) {
        return Coupon.builder()
                .name(name)
                .code(code)
                .description(description)
                .type(type)
                .discountPrice(discountPrice)
                .discountPercent(discountPercent)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .isPossibleDuplicatedApply(isPossibleDuplicatedApply)
                .build();
    }

    public String generateCouponCode(String tag) {
        int randomPartLength = 10 - tag.length();
        return tag + generateRandomAlphanumeric(randomPartLength);
    }

    private String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public void validateCouponExpiration(Coupon coupon) {
        if (coupon.isUsed()) {
            throw new CommonException(ErrorCode.USED_COUPON);
        }
        if (!coupon.isAvailable()) {
            throw new CommonException(ErrorCode.NOT_AVAILABLE_COUPON);
        }
    }

    public void applyCoupon(Coupon coupon, Order order) {
        if (!coupon.isPossibleDuplicatedApply()) {
            coupon.updateIsUsed(true);
        }
        if (coupon.getType() == ECouponType.DELIVERY_PRICE_FREE) {
            order.getDelivery().updateDeliveryPrice(0);
        }
    }
}
