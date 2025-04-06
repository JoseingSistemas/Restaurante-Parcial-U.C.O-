package com.restaurant.domain.model;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Order {
    private final UUID id;
    private final Integer tableNumber;
    private final List<OrderItem> items;
    private OrderStatus status;
    public boolean couponApplied;
    public double discountAmount;
    public double discountPercentage;
    private double total;

    public Order(Integer tableNumber) {
        this.id = UUID.randomUUID();
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.couponApplied = false;
        this.discountAmount = 0.0;
        this.discountPercentage = 0.0;
        this.total = 0.0;
    }

    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    public void addItem(Product product, Integer quantity) {
        if (product == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("El producto o la cantidad son inválidos.");
        }
        items.add(new OrderItem(product, quantity));
        updateTotal();
    }

    public void applyDiscount(double percentage) {
        if (couponApplied) {
            throw new IllegalStateException("Ya se ha aplicado un cupón a este pedido.");
        }
        if (percentage > 10 || percentage < 0) {
            throw new IllegalArgumentException("El descuento debe estar entre 0% y 10%.");
        }

        this.discountPercentage = percentage;
        this.discountAmount = calculateSubtotal() * (percentage / 100);
        this.couponApplied = true;
        updateTotal();
    }

    public void applyCoupon(Coupon coupon) {
        if (couponApplied) {
            throw new IllegalStateException("Este pedido ya tiene un cupón aplicado.");
        }
        applyDiscount(coupon.discountPercent());
    }

    public double calculateTotal() {
        return calculateSubtotal() - discountAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private void updateTotal() {
        this.total = calculateTotal();
    }

    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public String printDetails() {
        StringBuilder details = new StringBuilder();
        details.append("\n=================================\n");
        details.append(String.format("📋 Pedido #%s\n", id));
        details.append(String.format("🪑 Mesa: #%d\n", tableNumber));
        details.append(String.format("📊 Estado: %s\n", status));
        details.append("\n🍽️ Productos:\n");

        items.forEach(item ->
                details.append(String.format("- %2d x %-20s %10.2f\n",
                        item.quantity(),
                        item.product().getName(),
                        item.product().getPrice()))
        );

        // Calcula el subtotal sin descuento (suma de los items)
        double subtotal = calculateSubtotal();
        details.append(String.format("\n💵 Subtotal (sin descuento): %.2f\n", subtotal));

        // Muestra el descuento aplicado si es que hay
        if (couponApplied) {
            details.append(String.format("🎫 Descuento aplicado: -%.2f (%.2f%%)\n", discountAmount, discountPercentage));
        }

        // Calcula el total final restando el descuento del subtotal
        double finalTotal = subtotal - discountAmount;
        details.append(String.format("\n💰 PAGÓ UN TOTAL DE: %.2f\n", finalTotal));
        details.append("=================================\n");

        return details.toString();
    }

    public boolean isDiscountApplied() {
        return couponApplied || discountAmount > 0;
    }

}
