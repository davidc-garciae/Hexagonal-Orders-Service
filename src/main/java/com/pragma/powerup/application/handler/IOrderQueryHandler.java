package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.OrderPageResponseDto;

public interface IOrderQueryHandler {
  OrderPageResponseDto listByStatusAndRestaurant(
      Long restaurantId, String status, int page, int size);
}
