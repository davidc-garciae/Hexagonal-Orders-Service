package com.pragma.powerup.testdata;

import com.pragma.powerup.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Factory for creating test data objects for Orders Service.
 * Provides consistent test data across all test cases following DRY principles.
 */
public class TestDataFactory {

    // ==================== DOMAIN MODELS ====================

    // Order Test Data
    public static final Long ORDER_ID = 1L;
    public static final Long CUSTOMER_ID = 100L;
    public static final Long RESTAURANT_ID = 200L;
    public static final Long EMPLOYEE_ID = 300L;
    public static final Long OWNER_ID = 400L;
    public static final Long ADMIN_ID = 500L;
    public static final String EMPLOYEE_NAME = "Juan Pérez";
    public static final String RESTAURANT_NAME = "El Buen Sabor";
    public static final String CUSTOMER_PHONE = "+573001234567";
    public static final String SECURITY_PIN = "123456";
    public static final String INVALID_PIN = "0000";

    // Order States (Spanish enum values)
    public static final OrderStatus STATUS_PENDING = OrderStatus.PENDIENTE;
    public static final OrderStatus STATUS_IN_PREPARATION = OrderStatus.EN_PREPARACION;
    public static final OrderStatus STATUS_READY = OrderStatus.LISTO;
    public static final OrderStatus STATUS_DELIVERED = OrderStatus.ENTREGADO;
    public static final OrderStatus STATUS_CANCELLED = OrderStatus.CANCELADO;

    // Time Constants
    public static final LocalDateTime CREATION_TIME = LocalDateTime.of(2024, 1, 15, 10, 30);
    public static final LocalDateTime UPDATE_TIME = LocalDateTime.of(2024, 1, 15, 11, 0);

    // Metrics Constants
    public static final Double AVERAGE_PREPARATION_TIME = 25.5;
    public static final Double AVERAGE_DELIVERY_TIME = 15.2;
    public static final Integer TOTAL_ORDERS = 50;
    public static final Integer COMPLETED_ORDERS = 45;

    // ==================== ORDER FACTORIES ====================

    public static Order validOrder() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setCustomerId(CUSTOMER_ID);
        order.setRestaurantId(RESTAURANT_ID);
        order.setStatus(STATUS_PENDING);
        order.setCreatedAt(CREATION_TIME);
        order.setUpdatedAt(UPDATE_TIME);
        order.setItems(validOrderItems());
        return order;
    }

    public static Order orderInPreparation() {
        Order order = validOrder();
        order.setStatus(STATUS_IN_PREPARATION);
        order.setEmployeeId(EMPLOYEE_ID);
        return order;
    }

    public static Order orderReady() {
        Order order = validOrder();
        order.setStatus(STATUS_READY);
        order.setEmployeeId(EMPLOYEE_ID);
        return order;
    }

    public static Order orderDelivered() {
        Order order = validOrder();
        order.setStatus(STATUS_DELIVERED);
        order.setEmployeeId(EMPLOYEE_ID);
        order.setPin(SECURITY_PIN);
        return order;
    }

    public static Order orderCancelled() {
        Order order = validOrder();
        order.setStatus(STATUS_CANCELLED);
        return order;
    }

    public static Order orderWithStatus(OrderStatus status) {
        Order order = validOrder();
        order.setStatus(status);
        return order;
    }

    public static Order orderWithEmployee(Long employeeId) {
        Order order = validOrder();
        order.setEmployeeId(employeeId);
        order.setStatus(STATUS_IN_PREPARATION);
        return order;
    }

    // ==================== ORDER ITEM FACTORIES ====================

    public static List<OrderItem> validOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem1());
        items.add(orderItem2());
        return items;
    }

    public static OrderItem orderItem1() {
        return new OrderItem(10L, 2);
    }

    public static OrderItem orderItem2() {
        return new OrderItem(11L, 1);
    }

    public static OrderItem customOrderItem(Long dishId, Integer quantity) {
        return new OrderItem(dishId, quantity);
    }

    // ==================== USER MODEL FACTORIES ====================

    public static UserModel validCustomer() {
        return UserModel.builder()
                .id(CUSTOMER_ID)
                .name("Juan Pérez")
                .lastname("García")
                .email("juan.perez@email.com")
                .phone(CUSTOMER_PHONE)
                .role("CUSTOMER")
                .build();
    }

    public static UserModel validEmployee() {
        return UserModel.builder()
                .id(EMPLOYEE_ID)
                .name("María López")
                .lastname("Martínez")
                .email("maria.lopez@restaurant.com")
                .phone("+573007654321")
                .role("EMPLOYEE")
                .build();
    }

    public static UserModel userWithRole(Long userId, String role) {
        return UserModel.builder()
                .id(userId)
                .name("Test User")
                .lastname("Test Lastname")
                .email("test@email.com")
                .phone("+573001111111")
                .role(role)
                .build();
    }

    // ==================== TRACEABILITY FACTORIES ====================

    public static TraceabilityEvent orderCreatedEvent() {
        return TraceabilityEvent.builder()
                .id("1")
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .eventType("ORDER_CREATED")
                .previousStatus(null)
                .newStatus("PENDIENTE")
                .timestamp(CREATION_TIME)
                .build();
    }

    public static TraceabilityEvent orderAssignedEvent() {
        return TraceabilityEvent.builder()
                .id("2")
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .eventType("ORDER_ASSIGNED")
                .employeeId(EMPLOYEE_ID)
                .employeeName("María López")
                .previousStatus("PENDIENTE")
                .newStatus("EN_PREPARACION")
                .timestamp(UPDATE_TIME)
                .build();
    }

    public static TraceabilityEvent orderReadyEvent() {
        return TraceabilityEvent.builder()
                .id("3")
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .eventType("ORDER_READY")
                .employeeId(EMPLOYEE_ID)
                .employeeName("María López")
                .previousStatus("EN_PREPARACION")
                .newStatus("LISTO")
                .timestamp(UPDATE_TIME.plusMinutes(30))
                .build();
    }

    public static TraceabilityEvent orderDeliveredEvent() {
        return TraceabilityEvent.builder()
                .id("4")
                .orderId(ORDER_ID)
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .eventType("ORDER_DELIVERED")
                .employeeId(EMPLOYEE_ID)
                .employeeName("María López")
                .previousStatus("LISTO")
                .newStatus("ENTREGADO")
                .timestamp(UPDATE_TIME.plusMinutes(45))
                .build();
    }

    public static TraceabilityEvent customTraceabilityEvent(Long orderId, String previousStatus, String newStatus) {
        return TraceabilityEvent.builder()
                .orderId(orderId)
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .eventType("STATUS_CHANGE")
                .employeeId(EMPLOYEE_ID)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ==================== METRICS FACTORIES ====================

    public static RestaurantMetrics validRestaurantMetrics() {
        return RestaurantMetrics.builder()
                .restaurantId(RESTAURANT_ID)
                .restaurantName("Restaurante Test")
                .totalOrders(TOTAL_ORDERS)
                .averagePreparationTime(AVERAGE_PREPARATION_TIME.intValue())
                .averageDeliveryTime(AVERAGE_DELIVERY_TIME.intValue())
                .build();
    }

    public static EmployeeMetrics validEmployeeMetrics() {
        return EmployeeMetrics.builder()
                .employeeId(EMPLOYEE_ID)
                .employeeName("María López")
                .ordersProcessed(TOTAL_ORDERS)
                .averagePreparationTime(AVERAGE_PREPARATION_TIME.intValue())
                .averageDeliveryTime(AVERAGE_DELIVERY_TIME.intValue())
                .build();
    }

    public static EmployeeMetrics customEmployeeMetrics(Long employeeId, Integer avgTime, Integer total) {
        return EmployeeMetrics.builder()
                .employeeId(employeeId)
                .employeeName("Test Employee")
                .ordersProcessed(total)
                .averagePreparationTime(avgTime)
                .averageDeliveryTime(15)
                .build();
    }

    // ==================== PAGED RESULT FACTORIES ====================

    public static PagedResult<Order> pagedOrderResult() {
        PagedResult<Order> result = new PagedResult<>();
        result.setContent(List.of(validOrder(), orderInPreparation()));
        result.setTotalElements(2L);
        result.setTotalPages(1);
        result.setPage(0);
        result.setSize(10);
        return result;
    }

    public static PagedResult<TraceabilityEvent> pagedTraceabilityResult() {
        PagedResult<TraceabilityEvent> result = new PagedResult<>();
        result.setContent(List.of(orderCreatedEvent(), orderAssignedEvent()));
        result.setTotalElements(2L);
        result.setTotalPages(1);
        result.setPage(0);
        result.setSize(10);
        return result;
    }

    public static PagedResult<Order> emptyPagedResult() {
        PagedResult<Order> result = new PagedResult<>();
        result.setContent(List.of());
        result.setTotalElements(0L);
        result.setTotalPages(0);
        result.setPage(0);
        result.setSize(10);
        return result;
    }

    // ==================== UTILITY METHODS ====================

    public static List<Order> orderList() {
        return List.of(validOrder(), orderInPreparation(), orderReady());
    }

    public static List<Order> emptyOrderList() {
        return List.of();
    }

    public static List<TraceabilityEvent> traceabilityEventList() {
        return List.of(orderCreatedEvent(), orderAssignedEvent(), orderReadyEvent());
    }

    // ==================== NESTED STATIC CLASSES FOR CUSTOM BUILDERS
    // ====================

    public static class OrderBuilder {
        public static Order pending() {
            return validOrder();
        }

        public static Order inPreparation() {
            return orderInPreparation();
        }

        public static Order ready() {
            return orderReady();
        }

        public static Order delivered() {
            return orderDelivered();
        }

        public static Order cancelled() {
            return orderCancelled();
        }

        public static Order withId(Long id) {
            Order order = validOrder();
            order.setId(id);
            return order;
        }

        public static Order withCustomer(Long customerId) {
            Order order = validOrder();
            order.setCustomerId(customerId);
            return order;
        }

        public static Order withRestaurant(Long restaurantId) {
            Order order = validOrder();
            order.setRestaurantId(restaurantId);
            return order;
        }

        public static Order withEmployee(Long employeeId) {
            Order order = validOrder();
            order.setEmployeeId(employeeId);
            return order;
        }
    }

    // Additional helper methods for tests
    public static Order orderWithId(Long id) {
        Order order = validOrder();
        order.setId(id);
        return order;
    }

    public static List<OrderItem> emptyItemsList() {
        return List.of();
    }

    public static List<OrderItem> invalidQuantityItems() {
        return List.of(
                new OrderItem(10L, 0), // Invalid quantity
                new OrderItem(20L, 3));
    }

    // ==================== TRACEABILITY FACTORIES ====================

    public static TraceabilityEvent validTraceabilityEvent() {
        TraceabilityEvent event = new TraceabilityEvent();
        event.setOrderId(ORDER_ID);
        event.setCustomerId(CUSTOMER_ID);
        event.setRestaurantId(RESTAURANT_ID);
        event.setEventType("ORDER_CREATED");
        event.setNewStatus(STATUS_PENDING.name()); // Convert to String
        event.setTimestamp(CREATION_TIME);
        return event;
    }

    public static TraceabilityEvent traceabilityEventForCustomer(Long customerId) {
        TraceabilityEvent event = validTraceabilityEvent();
        event.setCustomerId(customerId);
        return event;
    }

    // ==================== DTO FACTORIES ====================

    public static com.pragma.powerup.application.dto.request.OrderCreateRequestDto validOrderCreateRequest() {
        com.pragma.powerup.application.dto.request.OrderCreateRequestDto request = new com.pragma.powerup.application.dto.request.OrderCreateRequestDto();
        request.setCustomerId(CUSTOMER_ID);
        request.setRestaurantId(RESTAURANT_ID);
        request.setItems(List.of(validOrderItemRequest()));
        return request;
    }

    public static com.pragma.powerup.application.dto.request.OrderCreateRequestDto.OrderItemRequestDto validOrderItemRequest() {
        com.pragma.powerup.application.dto.request.OrderCreateRequestDto.OrderItemRequestDto item = new com.pragma.powerup.application.dto.request.OrderCreateRequestDto.OrderItemRequestDto();
        item.setDishId(10L);
        item.setQuantity(2);
        return item;
    }

    public static com.pragma.powerup.application.dto.response.OrderResponseDto validOrderResponse() {
        com.pragma.powerup.application.dto.response.OrderResponseDto response = new com.pragma.powerup.application.dto.response.OrderResponseDto();
        response.setId(ORDER_ID);
        response.setCustomerId(CUSTOMER_ID);
        response.setRestaurantId(RESTAURANT_ID);
        response.setStatus(STATUS_PENDING.name());
        response.setCreatedAt(CREATION_TIME);
        response.setItems(List.of(validOrderItemResponse()));
        return response;
    }

    public static com.pragma.powerup.application.dto.response.OrderResponseDto.OrderItemResponseDto validOrderItemResponse() {
        com.pragma.powerup.application.dto.response.OrderResponseDto.OrderItemResponseDto item = new com.pragma.powerup.application.dto.response.OrderResponseDto.OrderItemResponseDto();
        item.setDishId(10L);
        item.setQuantity(2);
        return item;
    }
}
