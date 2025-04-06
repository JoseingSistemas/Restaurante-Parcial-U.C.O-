package com.restaurant;

import com.restaurant.application.console.ConsoleMenu;
import com.restaurant.application.console.OrderConsoleAdapter;
import com.restaurant.application.console.ProductConsoleAdapter;
import com.restaurant.application.file.FileProductLoader;
import com.restaurant.domain.repository.CouponRepository;
import com.restaurant.domain.repository.OrderRepository;
import com.restaurant.domain.repository.ProductRepository;
import com.restaurant.domain.service.DiscountService;
import com.restaurant.infrastructure.InMemoryCouponRepository;
import com.restaurant.infrastructure.InMemoryOrderRepository;
import com.restaurant.infrastructure.InMemoryProductRepository;
import com.restaurant.usecase.OrderUseCase;
import com.restaurant.usecase.ProductUseCase;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleMenu mainMenu = createMainMenu();
        mainMenu.showMainMenu();
    }

    private static ConsoleMenu createMainMenu() {
        // Repositorios en memoria
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new InMemoryOrderRepository();
        CouponRepository couponRepository = new InMemoryCouponRepository();

        // Cargar productos desde archivo
        FileProductLoader productLoader = new FileProductLoader(productRepository);
        productLoader.loadProducts("menu.txt");

        // Servicios y casos de uso
        DiscountService discountService = new DiscountService(couponRepository);
        ProductUseCase productUseCase = new ProductUseCase(productRepository);
        OrderUseCase orderUseCase = new OrderUseCase(orderRepository, couponRepository, discountService);

        // Adaptadores de consola
        ProductConsoleAdapter productAdapter = new ProductConsoleAdapter(productUseCase);
        OrderConsoleAdapter orderAdapter = new OrderConsoleAdapter(orderUseCase, productUseCase);

        // Scanner y menú principal
        Scanner scanner = new Scanner(System.in);
        return new ConsoleMenu(productAdapter, orderAdapter, scanner);
    }
}


//Ulitmos cambios para la branch

