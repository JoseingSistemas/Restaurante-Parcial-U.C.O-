package com.restaurant.domain.repository;

import com.restaurant.domain.model.Coupon;
import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    List<Coupon> getAllCoupons();
    Optional<Coupon> findByCode(String code);
}
