package com.pragma.powerup.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> items;

    @Getter
    @Setter
    public static class OrderItemResponseDto {
        private Long dishId;
        private Integer quantity;
    }
}
