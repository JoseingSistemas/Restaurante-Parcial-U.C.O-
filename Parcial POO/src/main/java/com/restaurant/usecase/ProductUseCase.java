package com.restaurant.usecase;

import com.restaurant.domain.model.Product;
import com.restaurant.domain.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductUseCase {
    private final ProductRepository productRepository;

    public ProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(String name, Double price, String category) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }

        Product product = Product.builder()
                .name(name.trim())
                .price(price)
                .category(category.toUpperCase())
                .build();

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category.toUpperCase());
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Product product) {
        if (productRepository.findById(product.getId()).isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        return productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}