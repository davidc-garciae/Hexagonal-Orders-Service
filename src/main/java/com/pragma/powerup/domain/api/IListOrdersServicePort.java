package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;

public interface IListOrdersServicePort {
  PagedResult<Order> listByStatusAndRestaurant(
      Long restaurantId, OrderStatus status, int page, int size);

  PagedResult<Order> listByCustomer(Long customerId, OrderStatus status, int page, int size);
}
