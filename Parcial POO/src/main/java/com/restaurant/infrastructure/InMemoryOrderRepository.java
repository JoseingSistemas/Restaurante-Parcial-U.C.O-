package com.restaurant.infrastructure;

import com.restaurant.domain.model.Order;
import com.restaurant.domain.repository.OrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public void deleteById(UUID id) {
        orders.remove(id);
    }
}