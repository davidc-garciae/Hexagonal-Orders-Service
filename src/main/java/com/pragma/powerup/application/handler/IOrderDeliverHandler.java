package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderDeliverRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;

public interface IOrderDeliverHandler {
  OrderResponseDto deliver(Long orderId, OrderDeliverRequestDto request, Long employeeId);
}
