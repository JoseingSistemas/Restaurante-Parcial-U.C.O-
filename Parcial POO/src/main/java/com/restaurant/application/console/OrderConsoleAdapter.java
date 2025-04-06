package com.restaurant.application.console;

import com.restaurant.domain.model.Order;
import com.restaurant.domain.model.OrderStatus;
import com.restaurant.domain.model.Product;
import com.restaurant.usecase.OrderUseCase;
import com.restaurant.usecase.ProductUseCase;

import java.text.NumberFormat;
import java.util.*;

public class OrderConsoleAdapter {
    private final OrderUseCase orderUseCase;
    private final ProductUseCase productUseCase;
    private final Scanner scanner;
    private final NumberFormat currencyFormat;

    public OrderConsoleAdapter(OrderUseCase orderUseCase, ProductUseCase productUseCase) {
        this.orderUseCase = orderUseCase;
        this.productUseCase = productUseCase;
        this.scanner = new Scanner(System.in);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    }

    public void createOrder() {
        System.out.println("\n=== 🆕 NUEVO PEDIDO ===");

        try {
            // 1. Obtener número de mesa
            int tableNumber = readTableNumber();

            // 2. Crear pedido
            Order order = orderUseCase.createOrder(tableNumber);
            System.out.printf("\n✅ Pedido creado para mesa #%d. ID: %s%n", tableNumber, order.getId());

            // 3. Añadir productos
            addItemsToOrder(order.getId());

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n⚠️ Error inesperado al crear el pedido");
        } finally {
            pause();
        }
    }

    public void showActiveOrders() {
        System.out.println("\n=== 📋 PEDIDOS ACTIVOS ===");
        List<Order> activeOrders = orderUseCase.getActiveOrders();

        if (activeOrders.isEmpty()) {
            System.out.println("\nNo hay pedidos activos en este momento");
            return;
        }

        activeOrders.forEach(this::printOrderDetails);
        pause();
    }

    public void closeOrder() {
        System.out.println("\n=== 💰 CERRAR PEDIDO ===");
        showActiveOrders();

        try {
            // 1. Seleccionar pedido
            UUID orderId = selectOrderId("cerrar");
            if (orderId == null) return;

            // 2. Aplicar descuento si existe
            System.out.print("\n¿Tiene un cupón de descuento? (deje vacío si no): ");
            String coupon = scanner.nextLine();

            // 3. Cerrar pedido
            Order closedOrder = orderUseCase.closeOrder(orderId, coupon.isEmpty() ? null : coupon);

            // 4. Mostrar resumen
            printReceipt(closedOrder);

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n⚠️ Error inesperado al cerrar el pedido");
        } finally {
            pause();
        }
    }
    private void printReceipt(Order order) {
        System.out.println("\n=== 🧾 RECIBO DE PAGO ===");
        printOrderDetails(order);

        System.out.printf("\n💰 TOTAL A PAGAR: %s%n",
                currencyFormat.format(order.calculateTotal()));
    }

    public void markOrderAsDelivered() {
        System.out.print("Ingrese el ID del pedido a marcar como entregado: ");
        String orderIdInput = scanner.nextLine().trim();

        try {
            UUID orderId = UUID.fromString(orderIdInput);
            Optional<Order> orderOpt = orderUseCase.getOrderById(orderId);

            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();

                if (order.getStatus() == OrderStatus.IN_PROGRESS) {
                    order.changeStatus(OrderStatus.DELIVERED);
                    System.out.println("✅ El pedido ha sido marcado como ENTREGADO.");
                } else {
                    System.out.println("⚠️ El pedido no está en progreso y no se puede marcar como entregado.");
                }
            } else {
                System.out.println("❌ No se encontró un pedido con ese ID.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ID de pedido inválido.");
        }
    }

    public void cancelOrder() {
        System.out.println("\n=== ❌ CANCELAR PEDIDO ===");
        showActiveOrders();

        try {
            UUID orderId = selectOrderId("cancelar");
            if (orderId == null) return;

            boolean success = orderUseCase.cancelOrder(orderId);

            if (success) {
                System.out.println("\n✅ Pedido cancelado exitosamente");
            } else {
                System.out.println("\n⚠️ No se pudo cancelar el pedido. Puede que ya haya sido entregado.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n⚠️ Error inesperado al cancelar el pedido");
        } finally {
            pause();
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    private int readTableNumber() {
        while (true) {
            try {
                System.out.print("Ingrese el número de mesa (1-50): ");
                int tableNumber = scanner.nextInt();
                scanner.nextLine();

                if (tableNumber < 1 || tableNumber > 50) {
                    throw new IllegalArgumentException("Número de mesa inválido");
                }
                return tableNumber;
            } catch (InputMismatchException e) {
                System.out.println("Por favor ingrese un número válido");
                scanner.nextLine();
            }
        }
    }

    private void addItemsToOrder(UUID orderId) {
        boolean addingItems = true;

        while (addingItems) {
            System.out.println("\n🛒 Añadiendo productos al pedido:");
            productUseCase.getAllProducts().forEach(p ->
                    System.out.printf("- %s (%s)%n", p.getName(), currencyFormat.format(p.getPrice()))
            );

            System.out.print("\nIngrese el nombre del producto (o 'fin' para terminar): ");
            String productName = scanner.nextLine().trim().toLowerCase(); // Normaliza el nombre ingresado

            if (productName.equalsIgnoreCase("fin")) {
                addingItems = false;
                continue;
            }

            Optional<Product> product = productUseCase.getAllProducts().stream()
                    .filter(p -> p.getName().trim().toLowerCase().equals(productName)) // Normaliza el nombre en la comparación
                    .findFirst();

            if (product.isEmpty()) {
                System.out.println("⚠️ Producto no encontrado");
                continue;
            }

            try {
                System.out.print("Ingrese la cantidad: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();

                if (quantity <= 0) {
                    System.out.println("⚠️ La cantidad debe ser mayor a cero");
                    continue;
                }

                orderUseCase.addItemToOrder(orderId, product.get(), quantity);
                System.out.printf("✅ Añadido %d x %s al pedido%n", quantity, product.get().getName());

            } catch (InputMismatchException e) {
                System.out.println("⚠️ Cantidad inválida. Debe ser un número entero");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("⚠️ Error al añadir producto: " + e.getMessage());
            }
        }
    }

    private UUID selectOrderId(String action) {
        List<Order> activeOrders = orderUseCase.getActiveOrders();
        if (activeOrders.isEmpty()) {
            System.out.println("\nNo hay pedidos activos para " + action);
            return null;
        }

        while (true) {
            try {
                System.out.printf("\nIngrese el ID del pedido a %s (o 'salir' para cancelar): ", action);
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("salir")) {
                    return null;
                }

                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ ID inválido. Debe ser un UUID válido");
            }
        }
    }

    private void printOrderDetails(Order order) {
        System.out.println("\n=================================");
        System.out.printf("📋 Pedido #%s%n", order.getId());
        System.out.printf("🪑 Mesa: #%d%n", order.getTableNumber());
        System.out.printf("📊 Estado: %s%n", translateStatus(order.getStatus()));

        System.out.println("\n🍽️ Productos:");
        order.getItems().forEach(item ->
                System.out.printf("- %2d x %-20s %10s%n",
                        item.quantity(),
                        item.product().getName(),
                        currencyFormat.format(item.product().getPrice())));

        // Muestra el subtotal (sin descuento)
        System.out.println("\n💵 Subtotal: " + currencyFormat.format(order.calculateSubtotal()));

        // Si hay descuento, mostrarlo también acá
        if (order.isDiscountApplied()) {
            System.out.printf("🎫 Descuento aplicado: -%s (%.2f%%)%n",
                    currencyFormat.format(order.getDiscountAmount()),
                    order.getDiscountPercentage());
        }

        System.out.println("=================================");
    }


    private String translateStatus(OrderStatus status) {
        return switch (status) {
            case CREATED -> "🆕 Creado";
            case IN_PROGRESS -> "👨‍🍳 En preparación";
            case DELIVERED -> "✅ Entregado";
            case CANCELLED -> "❌ Cancelado";
            case CLOSED -> "💰 Cerrado/Pagado";
        };
    }

    private void pause() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}