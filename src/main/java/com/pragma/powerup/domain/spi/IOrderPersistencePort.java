package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;

public interface IOrderPersistencePort {
    boolean customerHasActiveOrder(Long customerId);

    Order save(Order order);

    boolean allDishesBelongToRestaurant(Long restaurantId, java.util.List<Long> dishIds);
}
