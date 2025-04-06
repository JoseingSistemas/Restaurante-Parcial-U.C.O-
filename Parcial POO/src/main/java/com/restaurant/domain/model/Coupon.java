package com.restaurant.domain.model;

public record Coupon(String code, double discountValue, double discountPercent) {

    public Coupon {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
    }

    public double getPercentAsDecimal() {
        return discountPercent / 100.0;
    }

}
