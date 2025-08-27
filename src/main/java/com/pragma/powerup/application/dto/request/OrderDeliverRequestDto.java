package com.pragma.powerup.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDeliverRequestDto {
  @NotBlank private String pin;
}
