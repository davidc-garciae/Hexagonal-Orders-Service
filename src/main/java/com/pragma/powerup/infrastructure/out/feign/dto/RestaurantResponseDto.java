package com.pragma.powerup.infrastructure.out.feign.dto;

import lombok.Data;

@Data
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String logoUrl;
    private Long ownerId;
}
