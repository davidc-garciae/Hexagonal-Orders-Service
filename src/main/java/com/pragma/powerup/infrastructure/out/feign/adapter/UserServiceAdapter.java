package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.spi.IUserServicePort;
import com.pragma.powerup.infrastructure.out.feign.client.IRestaurantServiceFeignClient;
import com.pragma.powerup.infrastructure.out.feign.client.IUserServiceFeignClient;
import com.pragma.powerup.infrastructure.out.feign.dto.RestaurantResponseDto;
import com.pragma.powerup.infrastructure.out.feign.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceAdapter implements IUserServicePort {

    private final IUserServiceFeignClient userServiceFeignClient;
    private final IRestaurantServiceFeignClient restaurantServiceFeignClient;

    @Override
    public boolean isOwnerOfRestaurant(Long userId, Long restaurantId) {
        try {
            RestaurantResponseDto restaurant = restaurantServiceFeignClient.getRestaurantById(restaurantId);

            if (restaurant != null) {
                return userId.equals(restaurant.getOwnerId());
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(
                    "Error checking restaurant ownership for user {} and restaurant {}: {}",
                    userId,
                    restaurantId,
                    e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isEmployeeOfRestaurant(Long userId, Long restaurantId) {
        try {
            return userServiceFeignClient.isEmployeeOfRestaurant(userId, restaurantId);
        } catch (Exception e) {
            // Log error and return safe default
            return false;
        }
    }

    @Override
    public String getUserName(Long userId) {
        try {
            UserResponseDto user = userServiceFeignClient.getUserById(userId);

            // Try name + surname first
            String firstName = user.getName();
            String lastName = user.getSurname();

            // If name/surname are null, try firstName/lastName
            if (firstName == null) {
                firstName = user.getFirstName();
            }
            if (lastName == null) {
                lastName = user.getLastName();
            }

            // If both are still null, use email as fallback
            if (firstName == null && lastName == null) {
                String email = user.getEmail();
                return email != null ? email.split("@")[0] : "Employee " + userId;
            }

            // Clean up null values
            String first = firstName != null ? firstName : "";
            String last = lastName != null ? lastName : "";

            return (first + " " + last).trim();
        } catch (Exception e) {
            // Log error and return fallback name
            return "Employee " + userId;
        }
    }

    @Override
    public String getRestaurantName(Long restaurantId) {
        try {
            return userServiceFeignClient.getRestaurantName(restaurantId);
        } catch (Exception e) {
            // Log error and return fallback name
            return "Restaurant " + restaurantId;
        }
    }
}
