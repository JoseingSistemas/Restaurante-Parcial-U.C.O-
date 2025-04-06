package com.restaurant.infrastructure;

import com.restaurant.domain.model.Product;
import com.restaurant.domain.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository implements ProductRepository {
    // Usamos ConcurrentHashMap para thread safety
    private final Map<UUID, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        // Si el producto no tiene ID, se asume que es nuevo
        if (product.getId() == null) {
            Product newProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .name(product.getName())
                    .price(product.getPrice())
                    .category(product.getCategory())
                    .build();

            products.put(newProduct.getId(), newProduct);
            return newProduct;
        }

        // Si ya existe, actualizamos
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public void saveAll(List<Product> productList) {
        if (productList == null) {
            throw new IllegalArgumentException("Product list cannot be null");
        }
        productList.forEach(this::save);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        return products.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        products.remove(id);
    }
}