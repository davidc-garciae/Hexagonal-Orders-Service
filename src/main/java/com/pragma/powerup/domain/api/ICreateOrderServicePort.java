package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Order;

public interface ICreateOrderServicePort {
  Order createOrder(Order order);
}
