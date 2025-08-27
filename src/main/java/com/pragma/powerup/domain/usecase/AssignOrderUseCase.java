package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;

public class AssignOrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;

    public AssignOrderUseCase(IOrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    public Order createOrder(Order order) {
        throw new UnsupportedOperationException("Not supported in this use case");
    }

    @Override
    public PagedResult<Order> listByStatusAndRestaurant(
            Long restaurantId, OrderStatus status, int page, int size) {
        throw new UnsupportedOperationException("Not supported in this use case");
    }

    @Override
    public Order assignOrder(Long orderId, Long employeeId) {
        Order order = orderPersistencePort
                .findById(orderId)
                .orElseThrow(() -> new DomainException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException("Only PENDIENTE orders can be assigned");
        }
        order.setEmployeeId(employeeId);
        order.setStatus(OrderStatus.EN_PREPARACION);
        return orderPersistencePort.save(order);
    }
}
