package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderCreateRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;

public interface IOrderHandler {
    OrderResponseDto createOrder(OrderCreateRequestDto request);
}
