package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.OrderResponseDto;

public interface IOrderCancelHandler {
  OrderResponseDto cancel(Long orderId, Long customerId);
}
