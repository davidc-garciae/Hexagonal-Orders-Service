package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;

public interface IOrderPersistencePort {
  boolean customerHasActiveOrder(Long customerId);

  Order save(Order order);

  boolean allDishesBelongToRestaurant(Long restaurantId, java.util.List<Long> dishIds);

  PagedResult<Order> findByRestaurantAndStatus(
      Long restaurantId, OrderStatus status, int page, int size);

  PagedResult<Order> findByCustomer(Long customerId, int page, int size);

  PagedResult<Order> findByCustomerAndStatus(
      Long customerId, OrderStatus status, int page, int size);

  java.util.Optional<Order> findById(Long orderId);
}
