package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;

public interface IDeliverOrderServicePort {
  Order deliver(Long orderId, String pin, Long employeeId);
}
