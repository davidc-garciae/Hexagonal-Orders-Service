package com.pragma.powerup.domain.spi;

public interface IUserServicePort {

  boolean isOwnerOfRestaurant(Long userId, Long restaurantId);

  boolean isEmployeeOfRestaurant(Long userId, Long restaurantId);

  String getUserName(Long userId);

  String getRestaurantName(Long restaurantId);
}
