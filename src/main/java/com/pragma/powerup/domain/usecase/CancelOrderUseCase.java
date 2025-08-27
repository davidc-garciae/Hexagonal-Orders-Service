package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;

public class CancelOrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;

    public CancelOrderUseCase(IOrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    public Order createOrder(Order order) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PagedResult<Order> listByStatusAndRestaurant(Long restaurantId, OrderStatus status, int page, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Order assignOrder(Long orderId, Long employeeId) {
        throw new UnsupportedOperationException();
    }

    public Order cancel(Long orderId, Long customerId) {
        Order o = orderPersistencePort.findById(orderId).orElseThrow(() -> new DomainException("Order not found"));
        if (!customerId.equals(o.getCustomerId())) {
            throw new DomainException("Forbidden");
        }
        if (o.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }
        o.setStatus(OrderStatus.CANCELADO);
        return orderPersistencePort.save(o);
    }
}
