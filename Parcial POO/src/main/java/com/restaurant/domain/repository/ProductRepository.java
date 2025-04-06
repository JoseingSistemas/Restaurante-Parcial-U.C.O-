package com.restaurant.domain.repository;

import com.restaurant.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    // Guarda un producto individual
    Product save(Product product);

    // Guarda una lista de productos
    void saveAll(List<Product> products);

    // Busca un producto por ID
    Optional<Product> findById(UUID id);

    // Obtiene todos los productos
    List<Product> findAll();

    // Busca productos por categoría
    List<Product> findByCategory(String category);

    // Elimina un producto
    void deleteById(UUID id);
}