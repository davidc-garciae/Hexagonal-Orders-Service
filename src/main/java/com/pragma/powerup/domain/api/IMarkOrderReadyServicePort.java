package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;

public interface IMarkOrderReadyServicePort {
  Order markReady(Long orderId, Long employeeId);
}
