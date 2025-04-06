package com.restaurant.application.console;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleMenu {
    private final ProductConsoleAdapter productAdapter;
    private final OrderConsoleAdapter orderAdapter;
    private final Scanner scanner;

    // Constantes actualizadas según nueva posición
    private static final int OPTION_VIEW_PRODUCTS = 1;
    private static final int OPTION_CREATE_ORDER = 2;
    private static final int OPTION_VIEW_ORDERS = 3;
    private static final int OPTION_MARK_DELIVERED = 4;
    private static final int OPTION_CLOSE_ORDER = 5;
    private static final int OPTION_CANCEL_ORDER = 6;
    private static final int OPTION_EXIT = 7;
    private static final int OPTION_ADMIN_MENU = 8;

    public ConsoleMenu(ProductConsoleAdapter productAdapter, OrderConsoleAdapter orderAdapter, Scanner scanner) {
        this.productAdapter = productAdapter;
        this.orderAdapter = orderAdapter;
        this.scanner = scanner;
    }

    public void showMainMenu() {
        boolean exit = false;

        while (!exit) {
            printMainMenuHeader();

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (option) {
                    case OPTION_VIEW_PRODUCTS -> showProductMenu();
                    case OPTION_CREATE_ORDER -> orderAdapter.createOrder();
                    case OPTION_VIEW_ORDERS -> orderAdapter.showActiveOrders();
                    case OPTION_MARK_DELIVERED -> orderAdapter.markOrderAsDelivered();
                    case OPTION_CLOSE_ORDER -> orderAdapter.closeOrder();
                    case OPTION_CANCEL_ORDER -> orderAdapter.cancelOrder();
                    case OPTION_EXIT -> exit = confirmExit();
                    case OPTION_ADMIN_MENU -> showAdminMenu();
                    default -> showInvalidOptionMessage();
                }
            } catch (InputMismatchException e) {
                showInvalidInputMessage();
                scanner.nextLine(); // Limpiar entrada inválida
            } catch (Exception e) {
                System.out.println("\n⚠️ Error: " + e.getMessage());
            }
        }

        showExitMessage();
    }

    private void printMainMenuHeader() {
        System.out.println("\n=== 🍽️ SISTEMA DE GESTIÓN DE PEDIDOS ===");
        System.out.println("1. 📋 Ver carta de productos");
        System.out.println("2. ➕ Realizar nuevo pedido");
        System.out.println("3. 📊 Ver pedidos activos");
        System.out.println("4. ✅ Marcar pedido como entregado");
        System.out.println("5. 💰 Cerrar pedido y calcular total");
        System.out.println("6. ❌ Cancelar pedido");
        System.out.println("7. 🚪 Salir del sistema");
        System.out.println("8. ⚙️  Menú de administración");
        System.out.print("👉 Seleccione una opción: ");
    }

    private void showProductMenu() {
        System.out.println("\n=== 📋 MENÚ DE PRODUCTOS ===");
        System.out.println("1. Ver todos los productos");
        System.out.println("2. Ver productos por categoría");
        System.out.print("👉 Seleccione opción: ");

        try {
            int subOption = scanner.nextInt();
            scanner.nextLine();

            switch (subOption) {
                case 1 -> productAdapter.showAllProducts();
                case 2 -> productAdapter.showProductsByCategory();
                default -> showInvalidOptionMessage();
            }
        } catch (InputMismatchException e) {
            showInvalidInputMessage();
            scanner.nextLine();
        }
    }

    private void showAdminMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== ⚙️ MENÚ DE ADMINISTRACIÓN ===");
            System.out.println("1. 🆕 Agregar nuevo producto");
            System.out.println("2. ✏️ Actualizar producto");
            System.out.println("3. 🗑️ Eliminar producto");
            System.out.println("4. 🔙 Volver al menú principal");
            System.out.print("👉 Seleccione opción: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1 -> productAdapter.addNewProduct();
                    case 2 -> productAdapter.updateProduct();
                    case 3 -> productAdapter.deleteProduct();
                    case 4 -> back = true;
                    default -> showInvalidOptionMessage();
                }
            } catch (InputMismatchException e) {
                showInvalidInputMessage();
                scanner.nextLine();
            }
        }
    }

    private boolean confirmExit() {
        System.out.print("\n¿Está seguro que desea salir? (S/N): ");
        String confirmation = scanner.nextLine();
        return confirmation.equalsIgnoreCase("S");
    }

    private void showInvalidOptionMessage() {
        System.out.println("\n❌ Opción no válida. Por favor intente nuevamente.");
        pause();
    }

    private void showInvalidInputMessage() {
        System.out.println("\n❌ Entrada inválida. Debe ingresar un número.");
        pause();
    }

    private void showExitMessage() {
        System.out.println("\n✅ Gracias por usar el Sistema de Gestión de Pedidos. ¡Hasta pronto! 👋");
    }

    private void pause() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}
