package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.OrderResponseDto;

public interface IOrderReadyHandler {
  OrderResponseDto markReady(Long orderId, Long employeeId);
}
