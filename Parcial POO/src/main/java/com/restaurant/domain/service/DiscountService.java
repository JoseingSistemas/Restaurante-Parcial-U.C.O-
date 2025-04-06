package com.restaurant.domain.service;

import com.restaurant.domain.model.Coupon;
import com.restaurant.domain.repository.CouponRepository;
import java.util.Optional;

public class DiscountService {
    private final CouponRepository couponRepository;

    public DiscountService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public double applyDiscount(double total, String couponCode) {
        Optional<Coupon> couponOpt = getCouponByCode(couponCode);

        if (couponOpt.isEmpty()) {
            throw new IllegalArgumentException("Cupón inválido");
        }

        Coupon coupon = couponOpt.get();
        double discountAmount = total * coupon.getPercentAsDecimal();
        return Math.max(total - discountAmount, 0);
    }
}
