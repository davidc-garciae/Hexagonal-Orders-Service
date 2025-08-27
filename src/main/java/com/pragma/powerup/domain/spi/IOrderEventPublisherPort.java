package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Order;

public interface IOrderEventPublisherPort {
  void publishOrderStatusChanged(Order order, String previousStatus);
}
