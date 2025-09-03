package com.pragma.powerup.infrastructure.out.feign.client;

import com.pragma.powerup.infrastructure.out.feign.dto.RestaurantResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service", url = "${feign.restaurant-service.url:http://localhost:8084}")
public interface IRestaurantServiceFeignClient {

    @GetMapping("/api/v1/restaurants/{restaurantId}")
    RestaurantResponseDto getRestaurantById(@PathVariable("restaurantId") Long restaurantId);
}
