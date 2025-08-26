package com.pragma.powerup.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private Long employeeId;
    private OrderStatus status;
    private String pin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items = new ArrayList<>();
}
