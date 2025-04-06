package com.restaurant.usecase;

import com.restaurant.domain.model.Coupon;
import com.restaurant.domain.model.Order;
import com.restaurant.domain.model.OrderStatus;
import com.restaurant.domain.model.Product;
import com.restaurant.domain.repository.CouponRepository;
import com.restaurant.domain.repository.OrderRepository;
import com.restaurant.domain.service.DiscountService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderUseCase {
    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final DiscountService discountService;

    public OrderUseCase(OrderRepository orderRepository, CouponRepository couponRepository, DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.discountService = discountService;
    }

    public Order createOrder(Integer tableNumber) {
        Order order = new Order(tableNumber);
        return orderRepository.save(order);
    }

    public Order addItemToOrder(UUID orderId, Product product, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("No se pueden añadir items a un pedido en estado " + order.getStatus());
        }

        order.addItem(product, quantity);
        order.changeStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    private void applyCouponDiscount(Order order, String couponCode) {
        Optional<Coupon> optionalCoupon = couponRepository.findByCode(couponCode);

        if (optionalCoupon.isEmpty()) {
            throw new IllegalArgumentException("Cupón inválido: " + couponCode);
        }

        Coupon coupon = optionalCoupon.get();
        if (coupon.discountPercent() > 10) {  // Si usas record, acceder con coupon.discountPercent()
            throw new IllegalArgumentException("El porcentaje del cupón no puede ser mayor al 10%.");
        }

        // Calcula el subtotal original del pedido
        double subtotal = order.calculateSubtotal();

        // Aplica el descuento usando DiscountService (ahora se calcula sobre el subtotal)
        double newTotal = discountService.applyDiscount(subtotal, couponCode);

        // Calcula el monto de descuento aplicado
        double discountAmount = subtotal - newTotal;

        // Actualiza los campos del pedido
        order.couponApplied = true;
        order.discountAmount = discountAmount;
        order.discountPercentage = coupon.discountPercent();
        order.setTotal(newTotal);
    }


    public Order closeOrder(UUID orderId, String couponCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe. ID: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Solo se pueden cerrar pedidos en estado 'Entregado'. Estado actual: " + order.getStatus());
        }

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            try {
                applyCouponDiscount(order, couponCode.trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error al aplicar el cupón: " + e.getMessage());
            }
        }

        order.changeStatus(OrderStatus.CLOSED);
        orderRepository.save(order);

        return order;
    }

    public boolean cancelOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CLOSED) {
                        return false;
                    }

                    order.changeStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    return true;
                })
                .orElse(false);
    }

    public List<Order> getActiveOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != OrderStatus.CLOSED &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .toList();
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}
