package com.pragma.powerup.infrastructure.out.feign.client;

import com.pragma.powerup.infrastructure.out.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${feign.user-service.url:http://localhost:8081}")
public interface IUserServiceFeignClient {

  @GetMapping("/api/v1/users/{userId}")
  UserResponseDto getUserById(@PathVariable("userId") Long userId);

  @GetMapping("/api/v1/restaurants/owner/{ownerId}")
  Long getRestaurantByOwnerId(@PathVariable("ownerId") Long ownerId);

  @GetMapping("/api/v1/restaurants/{restaurantId}/name")
  String getRestaurantName(@PathVariable("restaurantId") Long restaurantId);

  @GetMapping("/api/v1/users/{userId}/restaurant/{restaurantId}/is-owner")
  boolean isOwnerOfRestaurant(
      @PathVariable("userId") Long userId, @PathVariable("restaurantId") Long restaurantId);

  @GetMapping("/api/v1/users/{userId}/restaurant/{restaurantId}/is-employee")
  boolean isEmployeeOfRestaurant(
      @PathVariable("userId") Long userId, @PathVariable("restaurantId") Long restaurantId);
}
