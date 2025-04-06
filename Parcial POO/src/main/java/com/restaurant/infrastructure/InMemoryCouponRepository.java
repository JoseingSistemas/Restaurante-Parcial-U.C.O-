package com.restaurant.infrastructure;

import com.restaurant.domain.model.Coupon;
import com.restaurant.domain.repository.CouponRepository;
import java.util.List;
import java.util.Optional;

public class InMemoryCouponRepository implements CouponRepository {
    private final List<Coupon> coupons = List.of(
            new Coupon("DESCUENTO10", 0, 10),
            new Coupon("DESCUENTO5", 0, 5),
            new Coupon("BIENVENIDA", 0, 10)
    );

    @Override
    public List<Coupon> getAllCoupons() {
        return coupons;
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return coupons.stream()
                .filter(c -> c.code().equalsIgnoreCase(code.trim()))
                .findFirst();
    }
}
