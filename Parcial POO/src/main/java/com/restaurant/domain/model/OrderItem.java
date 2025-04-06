package com.restaurant.domain.model;

import lombok.Getter;

/**
 * @param product  Producto asociado al pedido
 * @param quantity Cantidad del producto
 */

public record OrderItem(Product product, int quantity) {
    public OrderItem {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
    }

    // Devuelve el subtotal (precio del producto * cantidad)
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%d x %s -> Subtotal: %.2f",
                quantity,
                product.getName(),
                getSubtotal());
    }
}