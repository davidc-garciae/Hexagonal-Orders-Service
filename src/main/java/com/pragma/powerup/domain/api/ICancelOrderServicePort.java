package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;

public interface ICancelOrderServicePort {
  Order cancel(Long orderId, Long customerId);
}
