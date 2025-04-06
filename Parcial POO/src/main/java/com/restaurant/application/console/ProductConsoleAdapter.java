package com.restaurant.application.console;

import com.restaurant.domain.model.Product;
import com.restaurant.usecase.ProductUseCase;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class ProductConsoleAdapter {
    private final ProductUseCase productUseCase;
    private final Scanner scanner;

    public ProductConsoleAdapter(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra todos los productos disponibles en la carta
     */
    public void showAllProducts() {
        List<Product> products = productUseCase.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("\nNo hay productos disponibles en la carta.");
            return;
        }

        System.out.println("\n=== CARTA COMPLETA ===");
        System.out.printf("%-20s %-10s %-15s%n", "NOMBRE", "PRECIO", "CATEGORÍA");
        System.out.println("----------------------------------------");

        products.forEach(product ->
                System.out.printf("%-20s $%-9.2f %-15s%n",
                        product.getName(),
                        product.getPrice(),
                        product.getCategory()));
    }

    /**
     * Muestra productos filtrados por categoría
     */
    public void showProductsByCategory() {
        System.out.println("\nCategorías disponibles:");
        System.out.println("1. Entradas");
        System.out.println("2. Platos fuertes");
        System.out.println("3. Bebidas");
        System.out.println("4. Postres");
        System.out.print("Seleccione una categoría (1-4): ");

        int categoryOption = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        String category;
        switch (categoryOption) {
            case 1 -> category = "ENTRADA";
            case 2 -> category = "PLATO_FUERTE";
            case 3 -> category = "BEBIDA";
            case 4 -> category = "POSTRE";
            default -> {
                System.out.println("Opción no válida");
                return;
            }
        }

        List<Product> products = productUseCase.getProductsByCategory(category);

        if (products.isEmpty()) {
            System.out.println("\nNo hay productos en esta categoría.");
            return;
        }

        System.out.printf("\n=== %s ===%n", category);
        products.forEach(product ->
                System.out.printf("- %s ($%.2f)%n", product.getName(), product.getPrice()));
    }

    /**
     * Permite seleccionar un producto de la carta
     * @return Optional con el producto seleccionado o empty si no se seleccionó
     */
    public Optional<Product> selectProduct() {
        showAllProducts();
        System.out.print("\nIngrese el nombre exacto del producto que desea seleccionar: ");
        String productName = scanner.nextLine();

        return productUseCase.getAllProducts().stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .findFirst();
    }

    /**
     * Muestra el menú de administración de productos
     */
    public void showProductManagementMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== GESTIÓN DE PRODUCTOS ===");
            System.out.println("1. Ver todos los productos");
            System.out.println("2. Ver productos por categoría");
            System.out.println("3. Agregar nuevo producto");
            System.out.println("4. Actualizar producto existente");
            System.out.println("5. Eliminar producto");
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (option) {
                case 1 -> showAllProducts();
                case 2 -> showProductsByCategory();
                case 3 -> addNewProduct();
                case 4 -> updateProduct();
                case 5 -> deleteProduct();
                case 6 -> exit = true;
                default -> System.out.println("Opción no válida");
            }
        }
    }

    /**
     * Agrega un nuevo producto a la carta
     */
    public void addNewProduct() {
        System.out.println("\n=== AGREGAR NUEVO PRODUCTO ===");

        System.out.print("Nombre del producto: ");
        String name = scanner.nextLine();

        System.out.print("Precio: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Limpiar buffer

        System.out.print("Categoría (ENTRADA/PLATO_FUERTE/BEBIDA/POSTRE): ");
        String category = scanner.nextLine().toUpperCase();

        Product newProduct = productUseCase.createProduct(name, price, category);
        System.out.printf("Producto '%s' agregado exitosamente con ID: %s%n",
                newProduct.getName(), newProduct.getId());
    }

    /**
     * Actualiza un producto existente
     */
    public void updateProduct() {
        System.out.println("\n=== ACTUALIZAR PRODUCTO ===");
        showAllProducts();

        System.out.print("\nIngrese el ID del producto a actualizar: ");
        String productId = scanner.nextLine();

        try {
            UUID id = UUID.fromString(productId);
            Optional<Product> existingProduct = productUseCase.getProductById(id);

            if (existingProduct.isEmpty()) {
                System.out.println("Producto no encontrado");
                return;
            }

            Product product = existingProduct.get();
            System.out.println("\nProducto seleccionado:");
            System.out.printf("Nombre: %s%n", product.getName());
            System.out.printf("Precio actual: $%.2f%n", product.getPrice());
            System.out.printf("Categoría actual: %s%n", product.getCategory());

            System.out.print("\nNuevo nombre (deje vacío para no cambiar): ");
            String newName = scanner.nextLine();

            System.out.print("Nuevo precio (0 para no cambiar): ");
            double newPrice = scanner.nextDouble();
            scanner.nextLine(); // Limpiar buffer

            System.out.print("Nueva categoría (deje vacío para no cambiar): ");
            String newCategory = scanner.nextLine().toUpperCase();

            // Crear producto actualizado
            Product updatedProduct = Product.builder()
                    .id(product.getId())
                    .name(newName.isBlank() ? product.getName() : newName)
                    .price(newPrice <= 0 ? product.getPrice() : newPrice)
                    .category(newCategory.isBlank() ? product.getCategory() : newCategory)
                    .build();

            productUseCase.updateProduct(updatedProduct);
            System.out.println("Producto actualizado exitosamente");

        } catch (IllegalArgumentException e) {
            System.out.println("ID de producto inválido");
        }
    }

    /**
     * Elimina un producto de la carta
     */
    public void deleteProduct() {
        System.out.println("\n=== ELIMINAR PRODUCTO ===");
        showAllProducts();

        System.out.print("\nIngrese el ID del producto a eliminar: ");
        String productId = scanner.nextLine();

        try {
            UUID id = UUID.fromString(productId);
            productUseCase.deleteProduct(id);
            System.out.println("Producto eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            System.out.println("ID de producto inválido");
        }
    }
}