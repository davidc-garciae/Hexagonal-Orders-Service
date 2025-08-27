package com.pragma.powerup.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequestDto {

  @NotNull private Long customerId;

  @NotNull private Long restaurantId;

  @NotEmpty private List<@Valid OrderItemRequestDto> items;

  @Getter
  @Setter
  public static class OrderItemRequestDto {
    @NotNull private Long dishId;

    @NotNull
    @Min(1)
    private Integer quantity;
  }
}
