package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;

public interface IOrderServicePort {
  Order createOrder(Order order);

  PagedResult<Order> listByStatusAndRestaurant(
      Long restaurantId, OrderStatus status, int page, int size);

  Order assignOrder(Long orderId, Long employeeId);
}
