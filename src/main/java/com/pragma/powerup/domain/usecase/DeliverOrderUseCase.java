package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;

public class DeliverOrderUseCase implements IOrderServicePort {

  private final IOrderPersistencePort orderPersistencePort;

  public DeliverOrderUseCase(IOrderPersistencePort orderPersistencePort) {
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
    throw new UnsupportedOperationException("Not supported in this use case");
  }

  public Order deliver(Long orderId, String pin) {
    Order order =
        orderPersistencePort
            .findById(orderId)
            .orElseThrow(() -> new DomainException("Order not found"));
    if (order.getStatus() != OrderStatus.LISTO) {
      throw new DomainException("Only LISTO orders can be delivered");
    }
    if (order.getPin() == null || !order.getPin().equals(pin)) {
      throw new DomainException("Invalid PIN");
    }
    order.setStatus(OrderStatus.ENTREGADO);
    return orderPersistencePort.save(order);
  }
}
