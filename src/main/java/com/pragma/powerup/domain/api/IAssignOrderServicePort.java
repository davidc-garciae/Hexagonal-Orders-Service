package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;

public interface IAssignOrderServicePort {
  Order assignOrder(Long orderId, Long employeeId);
}
