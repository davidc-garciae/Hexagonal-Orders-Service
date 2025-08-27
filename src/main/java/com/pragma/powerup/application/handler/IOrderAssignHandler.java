package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.OrderResponseDto;

public interface IOrderAssignHandler {
    OrderResponseDto assign(Long orderId, Long employeeId);
}
