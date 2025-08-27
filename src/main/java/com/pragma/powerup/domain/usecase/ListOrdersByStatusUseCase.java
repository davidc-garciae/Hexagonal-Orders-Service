package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;

public class ListOrdersByStatusUseCase implements IOrderServicePort {

  private final IOrderPersistencePort orderPersistencePort;

  public ListOrdersByStatusUseCase(IOrderPersistencePort orderPersistencePort) {
    this.orderPersistencePort = orderPersistencePort;
  }

  @Override
  public Order createOrder(Order order) {
    throw new UnsupportedOperationException("Not supported in this use case");
  }

  @Override
  public PagedResult<Order> listByStatusAndRestaurant(
      Long restaurantId, OrderStatus status, int page, int size) {
    if (restaurantId == null) throw new DomainException("restaurantId is required");
    if (status == null) throw new DomainException("status is required");
    if (page < 0 || size <= 0) throw new DomainException("invalid pagination parameters");
    return orderPersistencePort.findByRestaurantAndStatus(restaurantId, status, page, size);
  }
}
