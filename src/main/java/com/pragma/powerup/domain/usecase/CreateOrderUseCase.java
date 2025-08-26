package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;

    public CreateOrderUseCase(IOrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    public Order createOrder(Order order) {
        validateOrderRequest(order);

        if (orderPersistencePort.customerHasActiveOrder(order.getCustomerId())) {
            throw new DomainException("Customer already has an active order");
        }

        List<Long> dishIds = order.getItems().stream().map(OrderItem::getDishId).distinct().toList();
        if (!orderPersistencePort.allDishesBelongToRestaurant(order.getRestaurantId(), dishIds)) {
            throw new DomainException("All dishes must belong to the same restaurant");
        }

        order.setStatus(OrderStatus.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(order.getCreatedAt());

        return orderPersistencePort.save(order);
    }

    private void validateOrderRequest(Order order) {
        if (order == null) {
            throw new DomainException("Order is required");
        }
        if (order.getCustomerId() == null) {
            throw new DomainException("customerId is required");
        }
        if (order.getRestaurantId() == null) {
            throw new DomainException("restaurantId is required");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new DomainException("items must not be empty");
        }
        boolean anyInvalid = order.getItems().stream()
                .anyMatch(i -> i.getDishId() == null || i.getQuantity() == null || i.getQuantity() <= 0);
        if (anyInvalid) {
            throw new DomainException("Each item must have dishId and quantity > 0");
        }
    }
}
