package com.restaurant.application.file;

import com.restaurant.domain.model.Product;
import com.restaurant.domain.repository.ProductRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileProductLoader {
    private final ProductRepository productRepository;

    public FileProductLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void loadProducts(String filename) {
        List<Product> products = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Product product = Product.builder()
                            .name(parts[0].trim())
                            .price(Double.parseDouble(parts[1].trim()))
                            .category(parts[2].trim().toUpperCase())
                            .build();
                    products.add(product);
                }
            }
            productRepository.saveAll(products);
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
    }
}